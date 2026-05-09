package io.github.kroune.pollen.presentation.medications

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class RecentMedUi(
    val therapyId: Long,
    val name: String,
    val substance: String,
    val lastTaken: String,
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
    val recentMeds: ImmutableList<RecentMedUi>,
    val categories: ImmutableList<MedCategoryUi>,
    val todayDoses: ImmutableList<TodayDoseUi>,
    val todayCount: Int,
    val searchQuery: String,
    val isSheetExpanded: Boolean,
)
