package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapPolygonDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.MapRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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

class MapViewModel(
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        _state.value = MapUiState()
        viewModelScope.launch {
            val user = userRepository.getLocalUser()
            val userId = user?.serverId ?: 0L

            launch { loadPollens() }
            launch { loadPins(userId) }
            launch { loadHashtags() }
        }
    }

    private suspend fun loadPollens() {
        repeat(MAX_RETRIES) { attempt ->
            try {
                val pollens = pollenRepository.observePollens().first().toImmutableList()
                _state.value = _state.value.copy(pollens = LoadState.Loaded(pollens))
                if (pollens.isNotEmpty()) {
                    loadPolygons()
                }
                return
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.send(UiEvent.ShowError("Failed to load pollen types"))
                if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
            }
        }
        _state.value = _state.value.copy(pollens = LoadState.Failed)
    }

    private suspend fun loadPins(userId: Long) {
        repeat(MAX_RETRIES) { attempt ->
            try {
                when (val result = mapRepository.getPins(userId)) {
                    is ApiResult.Success -> {
                        _state.value = _state.value.copy(
                            pins = LoadState.Loaded(result.data.toImmutableList()),
                        )
                        return
                    }
                    is ApiResult.Error -> {
                        _events.send(UiEvent.ShowError("Failed to load map pins"))
                        if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.send(UiEvent.ShowError("Failed to load map pins"))
                if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
            }
        }
        _state.value = _state.value.copy(pins = LoadState.Failed)
    }

    private suspend fun loadHashtags() {
        repeat(MAX_RETRIES) { attempt ->
            try {
                when (val result = mapRepository.getHashTags()) {
                    is ApiResult.Success -> {
                        _state.value = _state.value.copy(
                            hashtags = LoadState.Loaded(result.data.toImmutableList()),
                        )
                        return
                    }
                    is ApiResult.Error -> {
                        _events.send(UiEvent.ShowError("Failed to load hashtags"))
                        if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.send(UiEvent.ShowError("Failed to load hashtags"))
                if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
            }
        }
        _state.value = _state.value.copy(hashtags = LoadState.Failed)
    }

    fun selectAllergen(index: Int) {
        _state.value = _state.value.copy(
            selectedAllergenIndex = index,
            polygons = LoadState.Loading,
            showAllergenDropdown = false,
        )
        viewModelScope.launch { loadPolygons() }
    }

    fun toggleHashtag(index: Int) {
        val current = _state.value.activeHashtagIndices
        _state.value = _state.value.copy(
            activeHashtagIndices = if (index in current) current - index else current + index,
        )
    }

    fun toggleAllergenDropdown() {
        _state.value = _state.value.copy(showAllergenDropdown = !_state.value.showAllergenDropdown)
    }

    fun dismissAllergenDropdown() {
        _state.value = _state.value.copy(showAllergenDropdown = false)
    }

    private suspend fun loadPolygons() {
        val pollens = _state.value.pollens.dataOrNull ?: return
        val idx = _state.value.selectedAllergenIndex
        if (pollens.isEmpty() || idx !in pollens.indices) return

        val pollenName = pollens[idx].name

        repeat(MAX_RETRIES) { attempt ->
            try {
                when (val result = mapRepository.getPolygonForecast(pollenName)) {
                    is ApiResult.Success -> {
                        _state.value = _state.value.copy(
                            polygons = LoadState.Loaded(result.data.toImmutableList()),
                        )
                        return
                    }
                    is ApiResult.Error -> {
                        _events.send(UiEvent.ShowError("Failed to load pollen forecast"))
                        if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.send(UiEvent.ShowError("Failed to load pollen forecast"))
                if (attempt < MAX_RETRIES - 1) delay(retryDelay(attempt))
            }
        }
        _state.value = _state.value.copy(polygons = LoadState.Failed)
    }

    companion object {
        private const val MAX_RETRIES = 3
    }
}

private fun retryDelay(attempt: Int): Long = 1000L * (1L shl attempt)
