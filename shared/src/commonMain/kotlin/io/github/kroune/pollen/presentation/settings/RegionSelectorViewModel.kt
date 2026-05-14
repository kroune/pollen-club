package io.github.kroune.pollen.presentation.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Stable
data class RegionSelectorUiState(
    val locations: LoadState<ImmutableList<LocationDomain>> = LoadState.Loading,
    val selectedLocationId: Int = 0,
    val searchQuery: String = "",
)

class RegionSelectorViewModel(
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(RegionSelectorUiState())
    val state: StateFlow<RegionSelectorUiState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(locations = LoadState.Loading)
            try {
                combine(
                    locationRepository.observeLocations(),
                    userRepository.observeUser(),
                    _searchQuery,
                ) { locations, user, query ->
                    val filtered = if (query.isBlank()) locations
                    else locations.filter { it.name.contains(query, ignoreCase = true) }
                    RegionSelectorUiState(
                        locations = LoadState.Loaded(filtered.toImmutableList()),
                        selectedLocationId = user?.location ?: 0,
                        searchQuery = query,
                    )
                }.collect { data ->
                    _state.value = data
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(locations = LoadState.Failed)
                _events.send(UiEvent.ShowError(MR.strings.error_update_region.desc()))
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectLocation(locationId: Int) {
        viewModelScope.launch {
            try {
                userRepository.updateLocation(locationId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_update_region.desc()))
            }
        }
    }
}
