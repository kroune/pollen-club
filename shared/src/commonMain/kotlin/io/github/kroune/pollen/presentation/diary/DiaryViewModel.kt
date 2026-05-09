package io.github.kroune.pollen.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.BodyZone
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.SymptomTagRegistry
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class DiaryViewModel(
    private val healthRepository: HealthRepository,
    private val userRepository: UserRepository,
    private val medicationRepository: MedicationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DiaryUiState())
    val state: StateFlow<DiaryUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _selectedDateStr = MutableStateFlow(today().toString())

    private var selectedDate: LocalDate = today()
    private var weekStart: LocalDate = today().startOfWeek()
    private var selectedFeeling: Feeling? = null
    private var selectedZone: BodyZone? = null
    private var selectedTagKeys = mutableSetOf<String>()
    private var currentEntryId: Long = 0
    private var loadJob: Job? = null

    init {
        rebuildUi()
        loadEntryForCurrentDate()
        observeTherapy()
    }

    fun selectDate(isoDate: String) {
        val date = LocalDate.parse(isoDate)
        if (date == selectedDate) return

        selectedDate = date
        if (date < weekStart || date >= weekStart.plus(7, DateTimeUnit.DAY)) {
            weekStart = date.startOfWeek()
        }
        resetForm()
        rebuildUi()
        _selectedDateStr.value = date.toString()
        loadEntryForCurrentDate()
    }

    fun navigateWeek(forward: Boolean) {
        weekStart = if (forward) {
            weekStart.plus(7, DateTimeUnit.DAY)
        } else {
            weekStart.minus(7, DateTimeUnit.DAY)
        }
        rebuildUi()
    }

    fun selectFeeling(feeling: Feeling) {
        selectedFeeling = feeling
        rebuildUi()
        saveEntry()
    }

    fun selectZone(zone: BodyZone) {
        selectedZone = if (selectedZone == zone) null else zone
        rebuildUi()
    }

    fun toggleTag(key: String) {
        if (key in selectedTagKeys) selectedTagKeys.remove(key) else selectedTagKeys.add(key)
        rebuildUi()
        saveEntry()
    }

    fun toggleMedicationTaken(therapyId: Long) {
        val item = _state.value.therapyItems.firstOrNull { it.therapyId == therapyId } ?: return
        viewModelScope.launch {
            try {
                medicationRepository.recordIntake(therapyId, selectedDate.toString(), !item.taken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError("Не удалось обновить приём"))
            }
        }
    }

    private fun resetForm() {
        selectedFeeling = null
        selectedZone = null
        selectedTagKeys.clear()
        currentEntryId = 0
    }

    private fun loadEntryForCurrentDate() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                val entry = healthRepository.getEntryByDate(selectedDate.toString())
                if (entry != null) {
                    currentEntryId = entry.id
                    selectedFeeling = entry.feeling
                    selectedTagKeys = entry.tags
                        .split(",")
                        .filter { it.isNotBlank() }
                        .toMutableSet()
                    selectedZone = inferPrimaryZone(selectedTagKeys)
                }
                rebuildUi()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError("Не удалось загрузить запись"))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTherapy() {
        viewModelScope.launch {
            _selectedDateStr.flatMapLatest { dateStr ->
                combine(
                    medicationRepository.observeTherapies(),
                    medicationRepository.observeIntakesForDate(dateStr),
                ) { therapies, intakes -> mapTherapyItems(therapies, intakes) }
            }.collect { items ->
                _state.value = _state.value.copy(therapyItems = items)
            }
        }
    }

    private fun rebuildUi() {
        _state.value = _state.value.copy(
            monthLabel = russianMonthLabel(weekStart),
            selectedIsoDate = selectedDate.toString(),
            dates = buildDates(weekStart, selectedDate),
            moodOptions = buildMoodOptions(selectedFeeling),
            bodyZones = mapBodyZones(selectedTagKeys, selectedZone),
            selectedZoneLabel = selectedZone?.let { "Симптомы · ${russianZoneLabel(it)}" },
            selectedZoneTags = mapZoneTags(selectedZone, selectedTagKeys),
        )
    }

    private fun saveEntry() {
        val feeling = selectedFeeling ?: return
        viewModelScope.launch {
            try {
                val tags = selectedTagKeys.toList()
                val entry = HealthEntryDomain(
                    id = currentEntryId,
                    date = selectedDate.toString(),
                    feeling = feeling,
                    tags = tags.joinToString(","),
                    eyes = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.EYES),
                    nose = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.NOSE),
                    throat = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.THROAT),
                    lungs = SymptomTagRegistry.deriveZoneSeverity(tags, BodyZone.CHEST),
                )
                val savedId = healthRepository.saveEntry(entry)
                if (currentEntryId == 0L) currentEntryId = savedId
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError("Не удалось сохранить запись"))
            }
        }
    }

    private fun inferPrimaryZone(tags: Set<String>): BodyZone? {
        return BodyZone.entries
            .maxByOrNull { zone -> tags.count { it.startsWith(zonePrefix(zone)) } }
            ?.takeIf { zone -> tags.any { it.startsWith(zonePrefix(zone)) } }
    }

    private fun today(): LocalDate =
        kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
