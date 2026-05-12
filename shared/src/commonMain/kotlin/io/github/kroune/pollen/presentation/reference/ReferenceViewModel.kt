package io.github.kroune.pollen.presentation.reference

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.home.PollenIconRegistry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.compose.resources.DrawableResource

data class ReferenceUiState(
    val allergens: LoadState<ImmutableList<ReferenceAllergenUi>> = LoadState.Loading,
    val searchQuery: String = "",
)

@Immutable
data class ReferenceAllergenUi(
    val pollenId: Int,
    val name: String,
    val nameEng: String,
    val description: String,
    val iconRes: DrawableResource?,
    val severityLevel: Int,
    val severityLabel: StringDesc,
)

class ReferenceViewModel(
    private val pollenRepository: PollenRepository,
    private val userRepository: UserRepository,
    private val todayProvider: TodayProvider,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ReferenceUiState> = combine(
        pollenRepository.observePollens(),
        pollenRepository.observeEnglishNames(),
        userRepository.observeUser().flatMapLatest { user ->
            val locationId = user?.location?.takeIf { it > 0 }
            if (locationId != null) {
                combine(
                    pollenRepository.observeLevelsForLocation(locationId),
                    pollenRepository.observeForecastsForLocation(locationId),
                ) { levels, forecasts ->
                    val levelMap = levels.associateBy { it.pollenId }
                    val forecastMap = forecasts.associateBy { it.pollenId }
                    levelMap.keys.union(forecastMap.keys).associateWith { pid ->
                        levelMap[pid] ?: forecastMap[pid]!!
                    }
                }
            } else {
                kotlinx.coroutines.flow.flowOf(emptyMap())
            }
        },
        _searchQuery,
    ) { pollens, engNames, levelMap, query ->
        val allergens = pollens.map { pollen ->
            val currentLevel = levelMap[pollen.id]?.value ?: 0
            val engName = engNames[pollen.id] ?: ""
            val label: StringDesc = if (currentLevel > 0) {
                pollen.levels.firstOrNull { it.level == currentLevel }?.name
                    ?.let { StringDesc.Raw(it) }
                    ?: MR.strings.status_not_active.desc()
            } else {
                MR.strings.status_not_active.desc()
            }
            ReferenceAllergenUi(
                pollenId = pollen.id,
                name = pollen.name,
                nameEng = engName,
                description = pollen.description,
                iconRes = PollenIconRegistry.iconFor(pollen.id),
                severityLevel = currentLevel,
                severityLabel = label,
            )
        }.toImmutableList()

        ReferenceUiState(
            allergens = LoadState.Loaded(allergens),
            searchQuery = query,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReferenceUiState())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
