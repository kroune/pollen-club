package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
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
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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

sealed interface MapIntent {
    data object LoadData : MapIntent
    data object CenterOnMyLocation : MapIntent
    data class LocationPermissionResult(val granted: Boolean) : MapIntent
    data object ShowLocationDisabledSnackbar : MapIntent
    data class SelectAllergen(val index: Int) : MapIntent
    data class ToggleHashtag(val index: Int) : MapIntent
    data object ToggleAllergenDropdown : MapIntent
    data object DismissAllergenDropdown : MapIntent
}

class MapViewModel(
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
    private val deviceLocationProvider: DeviceLocationProvider,
    private val locationRepository: LocationRepository,
) : MviViewModel<MapUiState, MapIntent, UiEvent>(MapUiState()) {

    private val _pins = MutableStateFlow<LoadState<ImmutableList<MapPinDomain>>>(LoadState.Loading)
    private val _tileIndex = MutableStateFlow<LoadState<TileRingIndex>>(LoadState.Loading)
    private val _hashtags = MutableStateFlow<LoadState<ImmutableList<HashTagDomain>>>(LoadState.Loading)

    init {
        viewModelScope.launch {
            pollenRepository.observePollens().collect { pollens ->
                updateState { copy(pollens = LoadState.Loaded(pollens.toImmutableList())) }
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
                updateState { copy(ringQuery = query) }
            }
        }
        viewModelScope.launch {
            _hashtags.collect { hashtags ->
                updateState { copy(hashtags = hashtags) }
                refilterPins()
            }
        }
        viewModelScope.launch {
            deviceLocationProvider.availability.collect { availability ->
                updateState { copy(locationAvailability = availability) }
            }
        }
        onIntent(MapIntent.LoadData)
    }

    override fun handleIntent(intent: MapIntent) {
        when (intent) {
            MapIntent.LoadData -> loadData()
            MapIntent.CenterOnMyLocation -> centerOnMyLocation()
            is MapIntent.LocationPermissionResult -> onLocationPermissionResult(intent.granted)
            MapIntent.ShowLocationDisabledSnackbar -> emitEffect(UiEvent.ShowError(MR.strings.error_location_disabled.desc()))
            is MapIntent.SelectAllergen -> selectAllergen(intent.index)
            is MapIntent.ToggleHashtag -> toggleHashtag(intent.index)
            MapIntent.ToggleAllergenDropdown -> updateState { copy(showAllergenDropdown = !showAllergenDropdown) }
            MapIntent.DismissAllergenDropdown -> updateState { copy(showAllergenDropdown = false) }
        }
    }

    private fun loadData() {
        _pins.value = LoadState.Loading
        _hashtags.value = LoadState.Loading
        _tileIndex.value = LoadState.Loading
        updateState {
            copy(
                pins = LoadState.Loading,
                hashtags = LoadState.Loading,
                ringQuery = LoadState.Loading,
                selectedAllergenIndex = 0,
                activeHashtagIndices = persistentSetOf(),
                showAllergenDropdown = false,
            )
        }
        viewModelScope.launch {
            runCatchingCancellable {
                val user = userRepository.getLocalUser()
                val userId = user?.serverId ?: 0L
                val regionId = user?.location ?: 0
                if (regionId > 0) {
                    val region = locationRepository.getById(regionId)
                    if (region != null) {
                        updateState {
                            copy(
                                centerLatitude = region.latitude,
                                centerLongitude = region.longitude,
                            )
                        }
                    }
                }
                launch { loadPins(userId) }
                launch { loadHashtags() }
                launch { loadPolygonsForCurrentAllergen() }
            }.onFailure {
                logger.e(it) { "Failed to initialize map data" }
                _pins.value = LoadState.Failed
                _hashtags.value = LoadState.Failed
                _tileIndex.value = LoadState.Failed
                updateState {
                    copy(
                        pins = LoadState.Failed,
                        hashtags = LoadState.Failed,
                        ringQuery = LoadState.Failed,
                    )
                }
                emitEffect(UiEvent.ShowError(MR.strings.error_something_went_wrong.desc()))
            }
        }
    }

    private suspend fun loadPins(userId: Long) {
        runCatchingCancellable {
            mapRepository.getPins(userId)
        }.onSuccess { result ->
            when (result) {
                is ApiResult.Success -> {
                    _pins.value = LoadState.Loaded(result.data.toImmutableList())
                    refilterPins()
                }
                is ApiResult.Error -> {
                    _pins.value = LoadState.Failed
                    updateState { copy(pins = LoadState.Failed) }
                    emitEffect(UiEvent.ShowError(MR.strings.error_load_map_pins.desc()))
                }
            }
        }.onFailure {
            logger.e(it) { "Failed to load map pins" }
            _pins.value = LoadState.Failed
            updateState { copy(pins = LoadState.Failed) }
            emitEffect(UiEvent.ShowError(MR.strings.error_load_map_pins.desc()))
        }
    }

    private suspend fun loadHashtags() {
        runCatchingCancellable {
            mapRepository.getHashTags()
        }.onSuccess { result ->
            when (result) {
                is ApiResult.Success -> _hashtags.value = LoadState.Loaded(result.data.toImmutableList())
                is ApiResult.Error -> {
                    _hashtags.value = LoadState.Failed
                    emitEffect(UiEvent.ShowError(MR.strings.error_load_hashtags.desc()))
                }
            }
        }.onFailure {
            logger.e(it) { "Failed to load hashtags" }
            _hashtags.value = LoadState.Failed
            emitEffect(UiEvent.ShowError(MR.strings.error_load_hashtags.desc()))
        }
    }

    private fun centerOnMyLocation() {
        if (deviceLocationProvider.availability.value != LocationAvailability.Available) return
        viewModelScope.launch {
            runCatchingCancellable {
                val coords = deviceLocationProvider.getCurrentLocation() ?: return@runCatchingCancellable
                updateState {
                    copy(
                        userLatitude = coords.latitude,
                        userLongitude = coords.longitude,
                        centerOnUserTrigger = centerOnUserTrigger + 1,
                    )
                }
            }.onFailure {
                logger.w(it) { "Failed to get current location" }
                emitEffect(UiEvent.ShowError(MR.strings.error_location_disabled.desc()))
            }
        }
    }

    private fun onLocationPermissionResult(granted: Boolean) {
        deviceLocationProvider.refreshAvailability()
        if (granted) centerOnMyLocation()
    }

    private fun selectAllergen(index: Int) {
        _tileIndex.value = LoadState.Loading
        updateState {
            copy(
                selectedAllergenIndex = index,
                ringQuery = LoadState.Loading,
                showAllergenDropdown = false,
            )
        }
        refilterPins()
        viewModelScope.launch { loadPolygonsForCurrentAllergen() }
    }

    private fun toggleHashtag(index: Int) {
        val current = currentState.activeHashtagIndices
        val updated = if (index in current) (current - index).toPersistentSet() else (current + index).toPersistentSet()
        updateState { copy(activeHashtagIndices = updated) }
        refilterPins()
    }

    private fun refilterPins() {
        val allPins = _pins.value.dataOrNull ?: return
        val pollens = (currentState.pollens.dataOrNull)
        val selectedPollenId = pollens?.getOrNull(currentState.selectedAllergenIndex)?.id
        val activeHashtags = currentState.activeHashtagIndices.mapNotNullTo(mutableSetOf()) {
            _hashtags.value.dataOrNull?.getOrNull(it)?.value
        }
        val filtered = filterPins(allPins, selectedPollenId, activeHashtags)
        updateState { copy(pins = LoadState.Loaded(filtered.toPlatformMapPins())) }
    }

    private suspend fun loadPolygonsForCurrentAllergen() {
        val pollens = pollenRepository.observePollens().first()
        val idx = currentState.selectedAllergenIndex
        if (pollens.isEmpty() || idx !in pollens.indices) {
            _tileIndex.value = LoadState.Loaded(TileRingIndex.EMPTY)
            return
        }
        val pollenId = pollens[idx].id

        runCatchingCancellable {
            mapRepository.getPolygonForecast(pollenId)
        }.onSuccess { result ->
            when (result) {
                is ApiResult.Success -> _tileIndex.value = LoadState.Loaded(TileRingIndex.build(result.data))
                is ApiResult.Error -> {
                    _tileIndex.value = LoadState.Failed
                    emitEffect(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
                }
            }
        }.onFailure {
            logger.e(it) { "Failed to load polygon forecast" }
            _tileIndex.value = LoadState.Failed
            emitEffect(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
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
