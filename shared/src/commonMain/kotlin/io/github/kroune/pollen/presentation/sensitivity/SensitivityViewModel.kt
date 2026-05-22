package io.github.kroune.pollen.presentation.sensitivity

import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface SensitivityIntent {
    data object LoadData : SensitivityIntent
    data class SetSensitivity(val pollenId: Int, val level: SensitivityLevel) : SensitivityIntent
}

class SensitivityViewModel(
    private val pollenRepository: PollenRepository,
    private val sensitivityRepository: SensitivityRepository,
) : MviViewModel<SensitivityUiState, SensitivityIntent, UiEvent>(SensitivityUiState()) {

    private var observeJob: Job? = null

    init {
        onIntent(SensitivityIntent.LoadData)
    }

    override fun handleIntent(intent: SensitivityIntent) {
        when (intent) {
            SensitivityIntent.LoadData -> loadData()
            is SensitivityIntent.SetSensitivity -> setSensitivity(intent.pollenId, intent.level)
        }
    }

    private fun loadData() {
        observeJob?.cancel()
        updateState { SensitivityUiState() }
        observeJob = viewModelScope.launch {
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
                updateState { copy(allergens = LoadState.Loaded(allergens)) }
            }
        }
    }

    private fun setSensitivity(pollenId: Int, level: SensitivityLevel) {
        viewModelScope.launch {
            runCatchingCancellable {
                sensitivityRepository.setSensitivity(pollenId, level)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_save_sensitivity.desc()))
            }
        }
    }
}
