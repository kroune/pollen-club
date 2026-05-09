package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class HomeDayForecastUi(
    val dayOfMonth: Int,
    val dayOfWeek: String,
    val severity: Int,
    val date: String,
)

@Immutable
data class HomeAllergenItemUi(
    val pollenId: Int,
    val name: String,
    val severity: Int,
)

@Immutable
data class HomeOtherAllergenUi(
    val pollenId: Int,
    val name: String,
)

@Immutable
data class HomePersonalIndexUi(
    val score: String,
    val severityLevel: Int,
    val label: String,
)

@Immutable
data class HomeUiData(
    val locationName: String,
    val personalIndex: HomePersonalIndexUi?,
    val dayForecasts: ImmutableList<HomeDayForecastUi>,
    val allergens: ImmutableList<HomeAllergenItemUi>,
    val otherAllergens: ImmutableList<HomeOtherAllergenUi>,
    val activeDayIndex: Int,
)
