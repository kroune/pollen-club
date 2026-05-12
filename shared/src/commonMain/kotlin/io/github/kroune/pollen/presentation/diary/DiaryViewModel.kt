package io.github.kroune.pollen.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.BodyZone
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.SymptomTagRegistry
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class DiaryViewModel(
    private val healthRepository: HealthRepository,
    private val userRepository: UserRepository,
    private val medicationRepository: MedicationRepository,
    private val todayProvider: TodayProvider,
    private val localeProvider: LocaleProvider,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _selectedDate = MutableStateFlow(todayProvider.today.value)
    private val _weekStart = MutableStateFlow(todayProvider.today.value.startOfWeek())
    private val _selectedFeeling = MutableStateFlow<Feeling?>(null)
    private val _selectedZone = MutableStateFlow<BodyZone?>(null)
    private val _selectedTagKeys = MutableStateFlow<Set<String>>(emptySet())

    init {
        observeTodayAdvance()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val entryFlow = _selectedDate.flatMapLatest { date ->
        healthRepository.observeEntryByDate(date.toString())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val therapyFlow = _selectedDate.flatMapLatest { date ->
        combine(
            medicationRepository.observeTherapies(),
            medicationRepository.observeIntakesForDate(date.toString()),
        ) { therapies, intakes -> mapTherapyItems(therapies, intakes) }
    }

    val state: StateFlow<DiaryUiState> = combine(
        _selectedDate,
        _weekStart,
        _selectedFeeling,
        _selectedZone,
    ) { date, weekStart, feeling, zone ->
        UiInputs(date, weekStart, feeling, zone)
    }.combine(_selectedTagKeys) { inputs, tags ->
        Pair(inputs, tags)
    }.combine(therapyFlow) { (inputs, tags), therapyItems ->
        Triple(inputs, tags, therapyItems)
    }.combine(localeProvider.currentLocale) { (inputs, tags, therapyItems), locale ->
        DiaryUiState(
            monthName = monthStringDesc(inputs.weekStart.month),
            selectedIsoDate = inputs.date.toString(),
            dates = buildDates(inputs.weekStart, inputs.date),
            moodOptions = buildMoodOptions(inputs.feeling),
            bodyZones = mapBodyZones(tags, inputs.zone),
            selectedZoneLabel = inputs.zone?.let { zoneStringDesc(it) },
            selectedZoneTags = mapZoneTags(inputs.zone, tags, locale),
            therapyItems = therapyItems,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiaryUiState())

    init {
        observeEntry()
    }

    private fun observeTodayAdvance() {
        viewModelScope.launch {
            todayProvider.today.collect { newToday ->
                val currentSelected = _selectedDate.value
                val yesterday = newToday.minus(1, DateTimeUnit.DAY)
                if (currentSelected == yesterday) {
                    _selectedDate.value = newToday
                    _weekStart.value = newToday.startOfWeek()
                }
            }
        }
    }

    private fun observeEntry() {
        viewModelScope.launch {
            entryFlow.collect { entry ->
                if (entry != null) {
                    _selectedFeeling.value = entry.feeling
                    _selectedTagKeys.value = entry.tags
                        .split(",")
                        .filter { it.isNotBlank() }
                        .toSet()
                    _selectedZone.value = inferPrimaryZone(_selectedTagKeys.value)
                }
            }
        }
    }

    fun selectDate(isoDate: String) {
        val date = LocalDate.parse(isoDate)
        if (date == _selectedDate.value) return

        _selectedDate.value = date
        if (date < _weekStart.value || date >= _weekStart.value.plus(7, DateTimeUnit.DAY)) {
            _weekStart.value = date.startOfWeek()
        }
        resetForm()
    }

    fun navigateWeek(forward: Boolean) {
        _weekStart.value = if (forward) {
            _weekStart.value.plus(7, DateTimeUnit.DAY)
        } else {
            _weekStart.value.minus(7, DateTimeUnit.DAY)
        }
    }

    fun selectFeeling(feeling: Feeling) {
        _selectedFeeling.value = feeling
        saveEntry()
    }

    fun selectZone(zone: BodyZone) {
        _selectedZone.value = if (_selectedZone.value == zone) null else zone
    }

    fun toggleTag(key: String) {
        val current = _selectedTagKeys.value.toMutableSet()
        if (key in current) current.remove(key) else current.add(key)
        _selectedTagKeys.value = current
        saveEntry()
    }

    fun toggleMedicationTaken(therapyId: Long) {
        viewModelScope.launch {
            try {
                val dateStr = _selectedDate.value.toString()
                val intakes = medicationRepository.observeIntakesForDate(dateStr)
                    .stateIn(viewModelScope).value
                val currentlyTaken = intakes.any { it.therapyId == therapyId && it.taken }
                medicationRepository.recordIntake(therapyId, dateStr, !currentlyTaken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_update_intake.desc()))
            }
        }
    }

    private fun resetForm() {
        _selectedFeeling.value = null
        _selectedZone.value = null
        _selectedTagKeys.value = emptySet()
    }

    private fun saveEntry() {
        val feeling = _selectedFeeling.value ?: return
        viewModelScope.launch {
            try {
                val tags = _selectedTagKeys.value.toList()
                val existingEntry = healthRepository.getEntryByDate(_selectedDate.value.toString())
                val entry = HealthEntryDomain(
                    id = existingEntry?.id ?: 0,
                    date = _selectedDate.value.toString(),
                    feeling = feeling,
                    tags = tags.joinToString(","),
                    eyes = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.EYES),
                    nose = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.NOSE),
                    throat = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.THROAT),
                    lungs = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.CHEST),
                )
                healthRepository.saveEntry(entry)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_save_entry.desc()))
            }
        }
    }

    private fun inferPrimaryZone(tags: Set<String>): BodyZone? {
        return BodyZone.entries
            .maxByOrNull { zone -> tags.count { it.startsWith(zonePrefix(zone)) } }
            ?.takeIf { zone -> tags.any { it.startsWith(zonePrefix(zone)) } }
    }
}

private data class UiInputs(
    val date: LocalDate,
    val weekStart: LocalDate,
    val feeling: Feeling?,
    val zone: BodyZone?,
)
