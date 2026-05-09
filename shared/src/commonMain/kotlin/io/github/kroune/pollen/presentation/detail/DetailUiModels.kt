package io.github.kroune.pollen.presentation.detail

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class DetailChartPointUi(
    val day: Int,
    val pollenValue: Float,
    val feelingValue: Float?,
)

@Immutable
data class DetailStatsUi(
    val peakDate: String,
    val declineDate: String,
    val symptomCount: Int,
)

@Immutable
data class DetailScreenDataUi(
    val allergenName: String,
    val currentScore: String?,
    val severityLabel: String,
    val severityLevel: Int,
    val chartPoints: ImmutableList<DetailChartPointUi>,
    val todayIndex: Int,
    val stats: DetailStatsUi?,
    val aboutText: String,
    val showFeelingLine: Boolean,
)
