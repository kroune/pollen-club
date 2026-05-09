package io.github.kroune.pollen.presentation.sensitivity

import androidx.compose.runtime.Stable
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.SensitivityLevel
import kotlinx.collections.immutable.ImmutableList

@Stable
data class SensitivityUiState(
    val allergens: LoadState<ImmutableList<SensitivityAllergenUi>> = LoadState.Loading,
)

data class SensitivityAllergenUi(
    val pollenId: Int,
    val name: String,
    val level: SensitivityLevel,
)
