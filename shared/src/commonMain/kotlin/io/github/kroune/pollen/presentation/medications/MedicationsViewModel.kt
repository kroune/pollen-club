package io.github.kroune.pollen.presentation.medications

import androidx.lifecycle.ViewModel
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

// TODO: replace mock data with real data from MedicationRepository
class MedicationsViewModel(
    private val medicationRepository: MedicationRepository,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(buildMockState())
    val state: StateFlow<MedicationsUiState> = _state.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun toggleSheetExpanded() {
        _state.update { it.copy(isSheetExpanded = !it.isSheetExpanded) }
    }

    fun toggleTakenToday(therapyId: Long) {
        _state.update { current ->
            current.copy(
                recentMeds = persistentListOf(
                    *current.recentMeds.map { med ->
                        if (med.therapyId == therapyId) med.copy(takenToday = !med.takenToday)
                        else med
                    }.toTypedArray(),
                ),
            )
        }
    }

    fun removeTodayDose(therapyId: Long) {
        _state.update { current ->
            val filtered = current.todayDoses.filter { it.therapyId != therapyId }
            current.copy(
                todayDoses = persistentListOf(*filtered.toTypedArray()),
                todayCount = filtered.size,
            )
        }
    }

    private fun buildMockState(): MedicationsUiState {
        val recentMeds = persistentListOf(
            RecentMedUi(1, "Цетрин", "Цетиризин · 10 мг · перорально", "вчера", 12, true),
            RecentMedUi(2, "Назонекс", "Мометазон · спрей в нос", "сегодня", 8, true),
            RecentMedUi(3, "Опатанол", "Олопатадин · капли в глаза", "3 дня назад", 4, false),
            RecentMedUi(4, "Сингуляр", "Монтелукаст · 10 мг", "неделю назад", 2, false),
        )
        val categories = persistentListOf(
            MedCategoryUi(1, "Системного действия"),
            MedCategoryUi(2, "Глаза"),
            MedCategoryUi(3, "Нос"),
            MedCategoryUi(4, "Бронхи"),
            MedCategoryUi(5, "Кожа"),
            MedCategoryUi(6, "Другие средства"),
        )
        val todayDoses = persistentListOf(
            TodayDoseUi(1, "Цетрин", "10 мг", 'Ц'),
            TodayDoseUi(2, "Назонекс", "2 впр.", 'Н'),
        )
        return MedicationsUiState(
            recentMeds = recentMeds,
            categories = categories,
            todayDoses = todayDoses,
            todayCount = todayDoses.size,
            searchQuery = "",
            isSheetExpanded = false,
        )
    }
}
