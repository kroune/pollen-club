package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.icerock.moko.resources.desc.RawStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.PollenDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

@Stable
data class HomeUiState(
    val selectedLocation: HomeLocationUi? = null,
    val locations: LoadState<ImmutableList<HomeLocationUi>> = LoadState.Loading,
    val pollens: LoadState<ImmutableList<PollenDomain>> = LoadState.Loading,
    val weather: LoadState<HomeWeatherUi> = LoadState.Loading,
    val dayForecasts: LoadState<ImmutableList<HomeDayForecastUi>> = LoadState.Loading,
    val personalIndex: LoadState<HomePersonalIndexUi?> = LoadState.Loading,
    val userAllergens: ImmutableList<AllergenRowData> = persistentListOf(),
    val otherAllergens: ImmutableList<HomeOtherAllergenUi> = persistentListOf(),
    val activeDayIndex: Int = 0,
    val showLocationPicker: Boolean = false,
    val expandedPollenId: Int? = null,
    val forecastTimeline: LoadState<ImmutableList<LevelDomain>> = LoadState.Loading,
    val today: LocalDate? = null,
    val isRefreshing: Boolean = false,
    val weekOffset: Int = 0,
    val weekLabel: StringDesc = RawStringDesc(""),
)

@Immutable
data class HomeLocationUi(
    val id: Int,
    val name: String,
)

@Immutable
data class HomeWeatherUi(
    val temperature: Double,
    val weatherCode: Int,
    val isDay: Boolean,
)

@Immutable
data class HomeOtherAllergenUi(
    val id: Int,
    val name: String,
)

@Immutable
data class HomeDayForecastUi(
    val dayOfMonth: Int,
    val dayOfWeek: StringDesc,
    val severity: Int,
    val date: LocalDate,
)

@Immutable
data class HomePersonalIndexUi(
    val score: Double,
    val severityLevel: Int,
    val label: StringDesc,
)

@Immutable
data class AllergenRowData(
    val pollen: PollenDomain,
    /** Current level on the universal 0..5 scale (= the raw server value). */
    val level: Int,
)
