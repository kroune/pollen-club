package io.github.kroune.pollen.presentation.settings.region

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Stable
data class RegionSelectorUiState(
    val locations: LoadState<ImmutableList<LocationDomain>> = LoadState.Loading,
    val selectedLocationId: Int? = null,
    val searchQuery: String = "",
)

sealed interface RegionSelectorIntent {
    data object LoadData : RegionSelectorIntent
    data class SearchQueryChanged(val query: String) : RegionSelectorIntent
    data class SelectLocation(val locationId: Int) : RegionSelectorIntent
}

class RegionSelectorViewModel(
    private val locationRepository: LocationRepository,
    private val userSession: UserSession,
) : MviViewModel<RegionSelectorUiState, RegionSelectorIntent, UiEvent>(RegionSelectorUiState()) {

    private val _searchQuery = MutableStateFlow("")
    private var loadJob: Job? = null

    init {
        onIntent(RegionSelectorIntent.LoadData)
    }

    override fun handleIntent(intent: RegionSelectorIntent) {
        when (intent) {
            RegionSelectorIntent.LoadData -> loadData()
            is RegionSelectorIntent.SearchQueryChanged -> {
                updateState { copy(searchQuery = intent.query) }
                _searchQuery.value = intent.query
            }
            is RegionSelectorIntent.SelectLocation -> selectLocation(intent.locationId)
        }
    }

    private data class RegionSlice(
        val locations: ImmutableList<LocationDomain>,
        val selectedLocationId: Int?,
    )

    private fun loadData() {
        loadJob?.cancel()
        updateState { copy(locations = LoadState.Loading) }
        loadJob = viewModelScope.launch {
            combine(
                locationRepository.observeLocations(),
                userSession.user,
                _searchQuery,
            ) { locations, user, query ->
                val filtered = if (query.isBlank()) locations
                else locations.filter { it.name.contains(query, ignoreCase = true) }
                RegionSlice(
                    locations = filtered.toImmutableList(),
                    selectedLocationId = user.location,
                )
            }.collect { slice ->
                updateState {
                    copy(
                        locations = LoadState.Loaded(slice.locations),
                        selectedLocationId = slice.selectedLocationId,
                    )
                }
            }
        }
    }

    private fun selectLocation(locationId: Int) {
        viewModelScope.launch {
            runCatchingCancellable {
                userSession.setLocation(locationId)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_update_region.desc()))
            }
        }
    }
}
