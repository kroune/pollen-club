package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.icerock.moko.resources.desc.RawStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.model.WeatherDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

@Stable
data class HomeUiState(
    val user: UserDomain? = null,
    val selectedLocation: LocationDomain? = null,
    val locations: LoadState<ImmutableList<LocationDomain>> = LoadState.Loading,
    val pollens: LoadState<ImmutableList<PollenDomain>> = LoadState.Loading,
    val weather: LoadState<WeatherDomain> = LoadState.Loading,
    val dayForecasts: LoadState<ImmutableList<HomeDayForecastUi>> = LoadState.Loading,
    val personalIndex: LoadState<HomePersonalIndexUi?> = LoadState.Loading,
    val userAllergens: ImmutableList<AllergenRowData> = persistentListOf(),
    val otherAllergens: ImmutableList<PollenDomain> = persistentListOf(),
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
data class HomeDayForecastUi(
    val dayOfMonth: Int,
    val dayOfWeek: StringDesc,
    val severity: Int,
    val date: String,
)

@Immutable
data class HomePersonalIndexUi(
    val score: String,
    val severityLevel: Int,
    val label: StringDesc,
)

@Immutable
data class AllergenRowData(
    val pollen: PollenDomain,
    val severity: Int,
    val maxLevel: Int,
)
