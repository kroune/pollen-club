package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.presentation.detail.DetailStatsUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.DrawableResource

@Stable
data class ForecastDetailUiState(
    val pollen: LoadState<ForecastDetailPollenUi> = LoadState.Loading,
    val timeline: LoadState<ImmutableList<LevelDomain>> = LoadState.Loading,
    val today: LocalDate = LocalDate(2000, 1, 1),
    val currentScore: String = "—",
    val currentScoreMax: String = SCORE_DISPLAY_MAX,
    val severityLevel: Int = 0,
    val severityLabel: String = "",
    val stats: DetailStatsUi? = null,
    val aboutText: String = "",
    val pollenIconRes: DrawableResource? = null,
    val showFeelingLine: Boolean = true,
    val feelingValues: ImmutableList<Int?> = persistentListOf(),
)

@Immutable
data class ForecastDetailPollenUi(
    val name: String,
    val maxLevel: Int,
    val severityLabels: Map<Int, String>,
)

internal const val SCORE_DISPLAY_MAX = "10"
