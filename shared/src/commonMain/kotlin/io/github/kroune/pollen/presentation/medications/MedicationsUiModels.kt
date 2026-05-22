package io.github.kroune.pollen.presentation.medications

import androidx.compose.runtime.Immutable
import io.github.kroune.pollen.domain.model.LoadState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

@Immutable
data class RecentMedUi(
    val therapyId: Long,
    val name: String,
    val substance: String,
    val lastTaken: LocalDate?,
    val count: Int,
    val takenToday: Boolean,
)

@Immutable
data class MedCategoryUi(
    val id: Int,
    val name: String,
)

@Immutable
data class TodayDoseUi(
    val therapyId: Long,
    val name: String,
    val dosage: String,
    val initial: Char,
)

@Immutable
data class MedicationsUiState(
    val recentMeds: LoadState<ImmutableList<RecentMedUi>> = LoadState.Loading,
    val categories: LoadState<ImmutableList<MedCategoryUi>> = LoadState.Loading,
    val todayDoses: ImmutableList<TodayDoseUi> = persistentListOf(),
    val todayCount: Int = 0,
    val searchQuery: String = "",
    val isSheetExpanded: Boolean = false,
    val today: LocalDate? = null,
)
