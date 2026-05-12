package io.github.kroune.pollen.presentation.sensitivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SensitivityViewModel(
    private val pollenRepository: PollenRepository,
    private val sensitivityRepository: SensitivityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SensitivityUiState())
    val state: StateFlow<SensitivityUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var observeJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        observeJob?.cancel()
        _state.value = SensitivityUiState()
        observeJob = viewModelScope.launch {
            try {
                combine(
                    pollenRepository.observePollens(),
                    sensitivityRepository.observeAll(),
                ) { pollens, sensitivities ->
                    val sensitivityMap = sensitivities.associateBy { it.pollenId }
                    pollens.map { pollen ->
                        SensitivityAllergenUi(
                            pollenId = pollen.id,
                            name = pollen.name,
                            level = sensitivityMap[pollen.id]?.level ?: SensitivityLevel.NONE,
                        )
                    }.toImmutableList()
                }.collect { allergens ->
                    _state.value = _state.value.copy(allergens = LoadState.Loaded(allergens))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(allergens = LoadState.Failed)
                _events.send(UiEvent.ShowError(MR.strings.error_load_allergens.desc()))
            }
        }
    }

    fun setSensitivity(pollenId: Int, level: SensitivityLevel) {
        viewModelScope.launch {
            try {
                sensitivityRepository.setSensitivity(pollenId, level)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_save_sensitivity.desc()))
            }
        }
    }
}
