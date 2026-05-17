package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.DEFAULT_CENTER_LATITUDE
import io.github.kroune.pollen.domain.model.DEFAULT_CENTER_LONGITUDE
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationAvailability
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.TileRingIndex
import io.github.kroune.pollen.domain.model.TileRingQuery
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.MapRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Stable
data class MapUiState(
    val pollens: LoadState<ImmutableList<PollenDomain>> = LoadState.Loading,
    val pins: LoadState<PlatformMapPins> = LoadState.Loading,
    val ringQuery: LoadState<TileRingQuery> = LoadState.Loading,
    val hashtags: LoadState<ImmutableList<HashTagDomain>> = LoadState.Loading,
    val selectedAllergenIndex: Int = 0,
    val activeHashtagIndices: ImmutableSet<Int> = persistentSetOf(),
    val showAllergenDropdown: Boolean = false,
    val centerLatitude: Double = DEFAULT_CENTER_LATITUDE,
    val centerLongitude: Double = DEFAULT_CENTER_LONGITUDE,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val centerOnUserTrigger: Int = 0,
    val locationAvailability: LocationAvailability = LocationAvailability.Unknown,
)

private val logger = Logger.withTag("MapViewModel")

class MapViewModel(
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
    private val deviceLocationProvider: DeviceLocationProvider,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(MapUiState())
    val state: StateFlow<MapUiState> = _state

    private val _pins = MutableStateFlow<LoadState<ImmutableList<MapPinDomain>>>(LoadState.Loading)
    private val _tileIndex = MutableStateFlow<LoadState<TileRingIndex>>(LoadState.Loading)
    private val _hashtags = MutableStateFlow<LoadState<ImmutableList<HashTagDomain>>>(LoadState.Loading)

    init {
        viewModelScope.launch {
            pollenRepository.observePollens().collect { pollens ->
                _state.value = _state.value.copy(pollens = LoadState.Loaded(pollens.toImmutableList()))
                refilterPins()
            }
        }
        viewModelScope.launch {
            _tileIndex.collect { tileIndex ->
                val query = when (tileIndex) {
                    is LoadState.Loading -> LoadState.Loading
                    is LoadState.Failed -> LoadState.Failed
                    is LoadState.Loaded -> LoadState.Loaded(TileRingQuery(tileIndex.data::query))
                }
                _state.value = _state.value.copy(ringQuery = query)
            }
        }
        viewModelScope.launch {
            _hashtags.collect { hashtags ->
                _state.value = _state.value.copy(hashtags = hashtags)
                refilterPins()
            }
        }
        viewModelScope.launch {
            deviceLocationProvider.availability.collect { availability ->
                _state.value = _state.value.copy(locationAvailability = availability)
            }
        }
        loadData()
    }

    fun loadData() {
        _pins.value = LoadState.Loading
        _hashtags.value = LoadState.Loading
        _tileIndex.value = LoadState.Loading
        _state.value = _state.value.copy(
            pins = LoadState.Loading,
            hashtags = LoadState.Loading,
            ringQuery = LoadState.Loading,
            selectedAllergenIndex = 0,
            activeHashtagIndices = persistentSetOf(),
            showAllergenDropdown = false,
        )
        viewModelScope.launch {
            try {
                val user = userRepository.getLocalUser()
                val userId = user?.serverId ?: 0L
                val regionId = user?.location ?: 0
                if (regionId > 0) {
                    val region = locationRepository.getById(regionId)
                    if (region != null) {
                        _state.value = _state.value.copy(
                            centerLatitude = region.latitude,
                            centerLongitude = region.longitude,
                        )
                    }
                }
                launch { loadPins(userId) }
                launch { loadHashtags() }
                launch { loadPolygonsForCurrentAllergen() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.e(e) { "Failed to initialize map data" }
                _pins.value = LoadState.Failed
                _state.value = _state.value.copy(pins = LoadState.Failed)
                _events.send(UiEvent.ShowError(MR.strings.error_load_map_pins.desc()))
            }
        }
    }

    private suspend fun loadPins(userId: Long) {
        try {
            when (val result = mapRepository.getPins(userId)) {
                is ApiResult.Success -> {
                    _pins.value = LoadState.Loaded(result.data.toImmutableList())
                    refilterPins()
                }
                is ApiResult.Error -> {
                    _pins.value = LoadState.Failed
                    _state.value = _state.value.copy(pins = LoadState.Failed)
                    _events.send(UiEvent.ShowError(MR.strings.error_load_map_pins.desc()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.e(e) { "Failed to load map pins" }
            _pins.value = LoadState.Failed
            _state.value = _state.value.copy(pins = LoadState.Failed)
            _events.send(UiEvent.ShowError(MR.strings.error_load_map_pins.desc()))
        }
    }

    private suspend fun loadHashtags() {
        try {
            when (val result = mapRepository.getHashTags()) {
                is ApiResult.Success -> _hashtags.value = LoadState.Loaded(result.data.toImmutableList())
                is ApiResult.Error -> {
                    _hashtags.value = LoadState.Failed
                    _events.send(UiEvent.ShowError(MR.strings.error_load_hashtags.desc()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.e(e) { "Failed to load hashtags" }
            _hashtags.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_hashtags.desc()))
        }
    }

    fun centerOnMyLocation() {
        if (deviceLocationProvider.availability.value != LocationAvailability.Available) return
        viewModelScope.launch {
            try {
                val coords = deviceLocationProvider.getCurrentLocation() ?: return@launch
                _state.value = _state.value.copy(
                    userLatitude = coords.latitude,
                    userLongitude = coords.longitude,
                    centerOnUserTrigger = _state.value.centerOnUserTrigger + 1,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.w(e) { "Failed to get current location" }
                _events.send(UiEvent.ShowError(MR.strings.error_location_disabled.desc()))
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        deviceLocationProvider.refreshAvailability()
        if (granted) centerOnMyLocation()
    }

    fun showLocationDisabledSnackbar() {
        viewModelScope.launch {
            _events.send(UiEvent.ShowError(MR.strings.error_location_disabled.desc()))
        }
    }

    fun selectAllergen(index: Int) {
        _tileIndex.value = LoadState.Loading
        _state.value = _state.value.copy(
            selectedAllergenIndex = index,
            ringQuery = LoadState.Loading,
            showAllergenDropdown = false,
        )
        refilterPins()
        viewModelScope.launch { loadPolygonsForCurrentAllergen() }
    }

    fun toggleHashtag(index: Int) {
        val current = _state.value.activeHashtagIndices
        val updated = if (index in current) (current - index).toPersistentSet() else (current + index).toPersistentSet()
        _state.value = _state.value.copy(activeHashtagIndices = updated)
        refilterPins()
    }

    fun toggleAllergenDropdown() {
        _state.value = _state.value.copy(showAllergenDropdown = !_state.value.showAllergenDropdown)
    }

    fun dismissAllergenDropdown() {
        _state.value = _state.value.copy(showAllergenDropdown = false)
    }

    private fun refilterPins() {
        val allPins = _pins.value.dataOrNull ?: return
        val pollens = (_state.value.pollens as? LoadState.Loaded)?.data
        val selectedPollenId = pollens?.getOrNull(_state.value.selectedAllergenIndex)?.id
        val activeHashtags = _state.value.activeHashtagIndices.mapNotNullTo(mutableSetOf()) {
            _hashtags.value.dataOrNull?.getOrNull(it)?.value
        }
        val filtered = filterPins(allPins, selectedPollenId, activeHashtags)
        _state.value = _state.value.copy(
            pins = LoadState.Loaded(filtered.toPlatformMapPins()),
        )
    }

    private suspend fun loadPolygonsForCurrentAllergen() {
        val pollens = pollenRepository.observePollens().first()
        val idx = _state.value.selectedAllergenIndex
        if (pollens.isEmpty() || idx !in pollens.indices) {
            _tileIndex.value = LoadState.Loaded(TileRingIndex.EMPTY)
            return
        }
        val pollenId = pollens[idx].id

        try {
            when (val result = mapRepository.getPolygonForecast(pollenId)) {
                is ApiResult.Success -> _tileIndex.value = LoadState.Loaded(TileRingIndex.build(result.data))
                is ApiResult.Error -> {
                    _tileIndex.value = LoadState.Failed
                    _events.send(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.e(e) { "Failed to load polygon forecast" }
            _tileIndex.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
        }
    }
}

private fun filterPins(
    pins: List<MapPinDomain>,
    pollenId: Int?,
    activeHashtags: Set<String>,
): List<MapPinDomain> = pins.filter { pin ->
    (pollenId == null || pin.pollenType == pollenId) &&
        (activeHashtags.isEmpty() || pin.tags.any { it in activeHashtags })
}
