package io.github.kroune.pollen.presentation.medications

import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.MedicationIntakeDomain
import io.github.kroune.pollen.domain.model.TherapyDomain
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

sealed interface MedicationsIntent {
    data class SearchQueryChanged(val query: String) : MedicationsIntent
    data object ToggleSheetExpanded : MedicationsIntent
    data class ToggleTakenToday(val therapyId: Long) : MedicationsIntent
    data class RemoveTodayDose(val therapyId: Long) : MedicationsIntent
}

class MedicationsViewModel(
    private val medicationRepository: MedicationRepository,
    private val todayProvider: TodayProvider,
) : MviViewModel<MedicationsUiState, MedicationsIntent, UiEvent>(MedicationsUiState()) {

    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeToday()
        observeTherapies()
        loadCategories()
    }

    private fun observeToday() {
        viewModelScope.launch {
            todayProvider.today.collect { today ->
                updateState { copy(today = today) }
            }
        }
    }

    override fun handleIntent(intent: MedicationsIntent) {
        when (intent) {
            is MedicationsIntent.SearchQueryChanged -> {
                searchQueryFlow.value = intent.query
                updateState { copy(searchQuery = intent.query) }
            }
            MedicationsIntent.ToggleSheetExpanded -> updateState { copy(isSheetExpanded = !isSheetExpanded) }
            is MedicationsIntent.ToggleTakenToday -> toggleTakenToday(intent.therapyId)
            is MedicationsIntent.RemoveTodayDose -> removeTodayDose(intent.therapyId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTherapies() {
        viewModelScope.launch {
            combine(
                medicationRepository.observeTherapies(),
                todayProvider.today.flatMapLatest { medicationRepository.observeIntakesForDate(it) },
                medicationRepository.observeAllTakenIntakes(),
                searchQueryFlow,
            ) { therapies, todayIntakes, takenIntakes, query ->
                mapTherapyData(therapies, todayIntakes, takenIntakes, query)
            }.collect { data ->
                updateState {
                    copy(
                        recentMeds = LoadState.Loaded(data.recentMeds),
                        todayDoses = data.todayDoses,
                        todayCount = data.todayDoses.size,
                    )
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            runCatchingCancellable {
                medicationRepository.getCureCatalog()
            }.onSuccess { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val categories = result.data.actionTypes.map { actionType ->
                            MedCategoryUi(id = actionType.id, name = actionType.name)
                        }.toImmutableList()
                        updateState { copy(categories = LoadState.Loaded(categories)) }
                    }
                    is ApiResult.Error -> {
                        updateState { copy(categories = LoadState.Failed) }
                        emitEffect(UiEvent.ShowError(MR.strings.error_load_categories.desc()))
                    }
                }
            }.onFailure {
                updateState { copy(categories = LoadState.Failed) }
                emitEffect(UiEvent.ShowError(MR.strings.error_load_categories.desc()))
            }
        }
    }

    private fun toggleTakenToday(therapyId: Long) {
        viewModelScope.launch {
            runCatchingCancellable {
                val today = todayProvider.today.value
                val intakes = medicationRepository.observeIntakesForDate(today).first()
                val currentlyTaken = intakes.any { it.therapyId == therapyId && it.taken }
                medicationRepository.recordIntake(therapyId, today, !currentlyTaken)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_update_intake.desc()))
            }
        }
    }

    private fun removeTodayDose(therapyId: Long) {
        viewModelScope.launch {
            runCatchingCancellable {
                val today = todayProvider.today.value
                medicationRepository.recordIntake(therapyId, today, false)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_remove_dose.desc()))
            }
        }
    }

    private fun mapTherapyData(
        therapies: List<TherapyDomain>,
        todayIntakes: List<MedicationIntakeDomain>,
        takenIntakes: List<MedicationIntakeDomain>,
        searchQuery: String,
    ): TherapyUiData {
        val todayMap = todayIntakes.associateBy { it.therapyId }
        val takenByTherapy = takenIntakes.groupBy { it.therapyId }

        val filteredTherapies = if (searchQuery.isBlank()) {
            therapies
        } else {
            therapies.filter { it.cureName.contains(searchQuery, ignoreCase = true) }
        }

        val recentMeds = filteredTherapies.map { therapy ->
            val taken = todayMap[therapy.id]?.taken == true
            val takenForThisTherapy = takenByTherapy[therapy.id].orEmpty()
            RecentMedUi(
                therapyId = therapy.id,
                name = therapy.cureName,
                substance = "${therapy.dose} · ${therapy.form}",
                lastTaken = takenForThisTherapy.maxOfOrNull { it.date },
                count = takenForThisTherapy.size,
                takenToday = taken,
            )
        }.toImmutableList()

        val todayDoses = therapies
            .filter { therapy -> todayMap[therapy.id]?.taken == true }
            .map { therapy ->
                TodayDoseUi(
                    therapyId = therapy.id,
                    name = therapy.cureName,
                    dosage = therapy.dose,
                    initial = therapy.cureName.firstOrNull() ?: '?',
                )
            }.toImmutableList()

        return TherapyUiData(recentMeds, todayDoses)
    }
}

private data class TherapyUiData(
    val recentMeds: ImmutableList<RecentMedUi>,
    val todayDoses: ImmutableList<TodayDoseUi>,
)
