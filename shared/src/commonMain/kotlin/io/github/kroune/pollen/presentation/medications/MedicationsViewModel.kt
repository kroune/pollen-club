package io.github.kroune.pollen.presentation.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.ApiResult
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.MedicationIntakeDomain
import io.github.kroune.pollen.domain.model.TherapyDomain
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MedicationsViewModel(
    private val medicationRepository: MedicationRepository,
    private val todayProvider: TodayProvider,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _isSheetExpanded = MutableStateFlow(false)
    private val _categories = MutableStateFlow<LoadState<ImmutableList<MedCategoryUi>>>(LoadState.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MedicationsUiState> = combine(
        combine(
            medicationRepository.observeTherapies(),
            todayProvider.today.flatMapLatest { date ->
                medicationRepository.observeIntakesForDate(date.toString())
            },
        ) { therapies, intakes -> mapTherapyData(therapies, intakes) },
        _categories,
        _searchQuery,
        _isSheetExpanded,
    ) { therapyData, categories, query, sheetExpanded ->
        MedicationsUiState(
            recentMeds = LoadState.Loaded(therapyData.recentMeds),
            categories = categories,
            todayDoses = therapyData.todayDoses,
            todayCount = therapyData.todayDoses.size,
            searchQuery = query,
            isSheetExpanded = sheetExpanded,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MedicationsUiState())

    init {
        loadCategories()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleSheetExpanded() {
        _isSheetExpanded.update { !it }
    }

    fun toggleTakenToday(therapyId: Long) {
        viewModelScope.launch {
            try {
                val today = todayProvider.today.value.toString()
                val intakes = medicationRepository.observeIntakesForDate(today)
                    .stateIn(viewModelScope).value
                val currentlyTaken = intakes.any { it.therapyId == therapyId && it.taken }
                medicationRepository.recordIntake(therapyId, today, !currentlyTaken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_update_intake.desc()))
            }
        }
    }

    fun removeTodayDose(therapyId: Long) {
        viewModelScope.launch {
            try {
                val today = todayProvider.today.value.toString()
                medicationRepository.recordIntake(therapyId, today, false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_remove_dose.desc()))
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                when (val result = medicationRepository.getCureCatalog()) {
                    is ApiResult.Success -> {
                        val categories = result.data.actionTypes.map { actionType ->
                            MedCategoryUi(id = actionType.id, name = actionType.name)
                        }.toImmutableList()
                        _categories.value = LoadState.Loaded(categories)
                    }
                    is ApiResult.Error -> {
                        _categories.value = LoadState.Failed
                        _events.send(UiEvent.ShowError(MR.strings.error_load_categories.desc()))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _categories.value = LoadState.Failed
                _events.send(UiEvent.ShowError(MR.strings.error_load_categories.desc()))
            }
        }
    }

    private fun mapTherapyData(
        therapies: List<TherapyDomain>,
        intakes: List<MedicationIntakeDomain>,
    ): TherapyUiData {
        val intakeMap = intakes.associateBy { it.therapyId }

        val recentMeds = therapies.map { therapy ->
            val taken = intakeMap[therapy.id]?.taken ?: false
            RecentMedUi(
                therapyId = therapy.id,
                name = therapy.cureName,
                substance = "${therapy.dose} · ${therapy.form}",
                lastTaken = therapy.startDate,
                count = 0,
                takenToday = taken,
            )
        }.toImmutableList()

        val todayDoses = therapies
            .filter { therapy -> intakeMap[therapy.id]?.taken == true }
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
