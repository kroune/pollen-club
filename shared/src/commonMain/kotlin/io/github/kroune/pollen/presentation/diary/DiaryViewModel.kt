package io.github.kroune.pollen.presentation.diary

import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.BodyZone
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.SymptomTagRegistry
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.domain.usecase.CoordinateResolver
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.startOfWeek
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

sealed interface DiaryIntent {
    data class SelectDate(val date: LocalDate) : DiaryIntent
    data class NavigateWeek(val forward: Boolean) : DiaryIntent
    data class SelectFeeling(val feeling: Feeling) : DiaryIntent
    data class SelectZone(val zone: BodyZone) : DiaryIntent
    data class ToggleTag(val key: String) : DiaryIntent
    data class ToggleMedicationTaken(val therapyId: Long) : DiaryIntent
}

class DiaryViewModel(
    private val healthRepository: HealthRepository,
    private val medicationRepository: MedicationRepository,
    private val todayProvider: TodayProvider,
    private val localeProvider: LocaleProvider,
    private val coordinateResolver: CoordinateResolver,
) : MviViewModel<DiaryUiState, DiaryIntent, UiEvent>(DiaryUiState()) {

    private val _selectedDate = MutableStateFlow(todayProvider.today.value)
    private val _weekStart = MutableStateFlow(todayProvider.today.value.startOfWeek())
    private val _selectedFeeling = MutableStateFlow<Feeling?>(null)
    private val _selectedZone = MutableStateFlow<BodyZone?>(null)
    private val _selectedTagKeys = MutableStateFlow<Set<String>>(emptySet())

    init {
        observeTodayAdvance()
        observeSavedEntry()
        observeSelectedDate()
        observeMonthName()
        observeWeekDates()
        observeMoodOptions()
        observeSelectedZoneLabel()
        observeBodyZones()
        observeSelectedZoneTags()
        observeTherapyItems()
    }

    override fun handleIntent(intent: DiaryIntent) {
        when (intent) {
            is DiaryIntent.SelectDate -> selectDate(intent.date)
            is DiaryIntent.NavigateWeek -> navigateWeek(intent.forward)
            is DiaryIntent.SelectFeeling -> selectFeeling(intent.feeling)
            is DiaryIntent.SelectZone -> toggleZone(intent.zone)
            is DiaryIntent.ToggleTag -> toggleTag(intent.key)
            is DiaryIntent.ToggleMedicationTaken -> toggleMedicationTaken(intent.therapyId)
        }
    }

    private fun observeSelectedDate() {
        viewModelScope.launch {
            _selectedDate.collect { date ->
                updateState { copy(selectedDate = date) }
            }
        }
    }

    private fun observeMonthName() {
        viewModelScope.launch {
            _weekStart.collect { weekStart ->
                updateState { copy(monthName = monthStringDesc(weekStart.month)) }
            }
        }
    }

    private fun observeWeekDates() {
        viewModelScope.launch {
            combine(_weekStart, _selectedDate) { weekStart, date ->
                buildDates(weekStart, date)
            }.collect { dates ->
                updateState { copy(dates = dates) }
            }
        }
    }

    private fun observeMoodOptions() {
        viewModelScope.launch {
            _selectedFeeling.collect { feeling ->
                updateState { copy(moodOptions = buildMoodOptions(feeling)) }
            }
        }
    }

    private fun observeSelectedZoneLabel() {
        viewModelScope.launch {
            _selectedZone.collect { zone ->
                updateState { copy(selectedZoneLabel = zone?.let { zoneStringDesc(it) }) }
            }
        }
    }

    private fun observeBodyZones() {
        viewModelScope.launch {
            combine(_selectedZone, _selectedTagKeys) { zone, tags ->
                mapBodyZones(tags, zone)
            }.collect { zones ->
                updateState { copy(bodyZones = zones) }
            }
        }
    }

    private fun observeSelectedZoneTags() {
        viewModelScope.launch {
            combine(
                _selectedZone,
                _selectedTagKeys,
                localeProvider.currentLocale,
            ) { zone, tags, locale ->
                mapZoneTags(zone, tags, locale)
            }.collect { tags ->
                updateState { copy(selectedZoneTags = tags) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTherapyItems() {
        viewModelScope.launch {
            _selectedDate.flatMapLatest { date ->
                combine(
                    medicationRepository.observeTherapies(),
                    medicationRepository.observeIntakesForDate(date),
                ) { therapies, intakes -> mapTherapyItems(therapies, intakes) }
            }.collect { items ->
                updateState { copy(therapyItems = items) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSavedEntry() {
        viewModelScope.launch {
            _selectedDate.flatMapLatest { date ->
                healthRepository.observeEntryByDate(date)
            }.collect { entry ->
                if (entry != null) {
                    _selectedFeeling.value = entry.feeling
                    val tags = entry.tags.split(",").filter { it.isNotBlank() }.toSet()
                    _selectedTagKeys.value = tags
                    _selectedZone.value = inferPrimaryZone(tags)
                }
            }
        }
    }

    private fun observeTodayAdvance() {
        viewModelScope.launch {
            todayProvider.today.collect { newToday ->
                val yesterday = newToday.minus(1, DateTimeUnit.DAY)
                if (_selectedDate.value == yesterday) {
                    _selectedDate.value = newToday
                    _weekStart.value = newToday.startOfWeek()
                }
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (date == _selectedDate.value) return
        _selectedDate.value = date
        if (date < _weekStart.value || date >= _weekStart.value.plus(7, DateTimeUnit.DAY)) {
            _weekStart.value = date.startOfWeek()
        }
        resetForm()
    }

    private fun navigateWeek(forward: Boolean) {
        _weekStart.value = if (forward) {
            _weekStart.value.plus(7, DateTimeUnit.DAY)
        } else {
            _weekStart.value.minus(7, DateTimeUnit.DAY)
        }
    }

    private fun selectFeeling(feeling: Feeling) {
        _selectedFeeling.value = feeling
        saveEntry()
    }

    private fun toggleZone(zone: BodyZone) {
        _selectedZone.value = if (_selectedZone.value == zone) null else zone
    }

    private fun toggleTag(key: String) {
        val current = _selectedTagKeys.value.toMutableSet()
        if (key in current) current.remove(key) else current.add(key)
        _selectedTagKeys.value = current
        saveEntry()
    }

    private fun toggleMedicationTaken(therapyId: Long) {
        viewModelScope.launch {
            runCatchingCancellable {
                val date = _selectedDate.value
                val intakes = medicationRepository.observeIntakesForDate(date).first()
                val currentlyTaken = intakes.any { it.therapyId == therapyId && it.taken }
                medicationRepository.recordIntake(therapyId, date, !currentlyTaken)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_update_intake.desc()))
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
            runCatchingCancellable {
                val tags = _selectedTagKeys.value.toList()
                val existingEntry = healthRepository.getEntryByDate(_selectedDate.value)
                val location = coordinateResolver.resolve()
                val entry = HealthEntryDomain(
                    id = existingEntry?.id ?: 0,
                    date = _selectedDate.value,
                    feeling = feeling,
                    tags = tags.joinToString(","),
                    eyes = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.EYES),
                    nose = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.NOSE),
                    throat = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.THROAT),
                    lungs = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.CHEST),
                    latitude = location?.latitude ?: 0.0,
                    longitude = location?.longitude ?: 0.0,
                    locationId = location?.regionId ?: 0,
                    locationName = location?.regionName ?: "",
                )
                healthRepository.saveEntry(entry)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_save_entry.desc()))
            }
        }
    }

    private fun inferPrimaryZone(tags: Set<String>): BodyZone? {
        return BodyZone.entries
            .maxByOrNull { zone -> tags.count { it.startsWith(zonePrefix(zone)) } }
            ?.takeIf { zone -> tags.any { it.startsWith(zonePrefix(zone)) } }
    }
}

