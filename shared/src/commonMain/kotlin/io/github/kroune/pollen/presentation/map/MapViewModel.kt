package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HashTagDomain
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapPolygonDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import co.touchlab.kermit.Logger
import io.github.kroune.pollen.domain.repository.MapRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
data class MapUiState(
    val pollens: LoadState<ImmutableList<PollenDomain>> = LoadState.Loading,
    val pins: LoadState<ImmutableList<MapPinDomain>> = LoadState.Loading,
    val polygons: LoadState<ImmutableList<MapPolygonDomain>> = LoadState.Loading,
    val hashtags: LoadState<ImmutableList<HashTagDomain>> = LoadState.Loading,
    val selectedAllergenIndex: Int = 0,
    val activeHashtagIndices: Set<Int> = emptySet(),
    val showAllergenDropdown: Boolean = false,
)

private val logger = Logger.withTag("MapViewModel")

class MapViewModel(
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _pins = MutableStateFlow<LoadState<ImmutableList<MapPinDomain>>>(LoadState.Loading)
    private val _polygons = MutableStateFlow<LoadState<ImmutableList<MapPolygonDomain>>>(LoadState.Loading)
    private val _hashtags = MutableStateFlow<LoadState<ImmutableList<HashTagDomain>>>(LoadState.Loading)
    private val _selectedAllergenIndex = MutableStateFlow(0)
    private val _activeHashtagIndices = MutableStateFlow<Set<Int>>(emptySet())
    private val _showAllergenDropdown = MutableStateFlow(false)

    val state: StateFlow<MapUiState> = combine(
        pollenRepository.observePollens().map { LoadState.Loaded(it.toImmutableList()) },
        _pins,
        _polygons,
        _hashtags,
        combine(_selectedAllergenIndex, _activeHashtagIndices, _showAllergenDropdown) { idx, hash, show ->
            Triple(idx, hash, show)
        },
    ) { pollens, pins, polygons, hashtags, (idx, hashIndices, showDropdown) ->
        MapUiState(
            pollens = pollens,
            pins = pins,
            polygons = polygons,
            hashtags = hashtags,
            selectedAllergenIndex = idx,
            activeHashtagIndices = hashIndices,
            showAllergenDropdown = showDropdown,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MapUiState())

    init {
        loadData()
    }

    fun loadData() {
        _pins.value = LoadState.Loading
        _hashtags.value = LoadState.Loading
        _polygons.value = LoadState.Loading
        viewModelScope.launch {
            val user = userRepository.getLocalUser()
            val userId = user?.serverId ?: 0L
            launch { loadPins(userId) }
            launch { loadHashtags() }
            launch { loadPolygonsForCurrentAllergen() }
        }
    }

    private suspend fun loadPins(userId: Long) {
        try {
            when (val result = mapRepository.getPins(userId)) {
                is ApiResult.Success -> _pins.value = LoadState.Loaded(result.data.toImmutableList())
                is ApiResult.Error -> {
                    _pins.value = LoadState.Failed
                    _events.send(UiEvent.ShowError(MR.strings.error_load_map_pins.desc()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.e(e) { "Failed to load map pins" }
            _pins.value = LoadState.Failed
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

    fun selectAllergen(index: Int) {
        _selectedAllergenIndex.value = index
        _polygons.value = LoadState.Loading
        _showAllergenDropdown.value = false
        viewModelScope.launch { loadPolygonsForCurrentAllergen() }
    }

    fun toggleHashtag(index: Int) {
        val current = _activeHashtagIndices.value
        _activeHashtagIndices.value = if (index in current) current - index else current + index
    }

    fun toggleAllergenDropdown() {
        _showAllergenDropdown.value = !_showAllergenDropdown.value
    }

    fun dismissAllergenDropdown() {
        _showAllergenDropdown.value = false
    }

    private suspend fun loadPolygonsForCurrentAllergen() {
        val pollens = pollenRepository.observePollens().first()
        val idx = _selectedAllergenIndex.value
        if (pollens.isEmpty() || idx !in pollens.indices) {
            _polygons.value = LoadState.Loaded(emptyList<MapPolygonDomain>().toImmutableList())
            return
        }
        val pollenName = pollens[idx].nameRus

        try {
            when (val result = mapRepository.getPolygonForecast(pollenName)) {
                is ApiResult.Success -> _polygons.value = LoadState.Loaded(result.data.toImmutableList())
                is ApiResult.Error -> {
                    _polygons.value = LoadState.Failed
                    _events.send(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.e(e) { "Failed to load polygon forecast" }
            _polygons.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
        }
    }
}
