package io.github.kroune.pollen.presentation.phenology

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PhenologyStageUi(
    val number: Int,
    val name: String,
    val description: String,
    val isDone: Boolean,
    val isCurrent: Boolean,
)

@Immutable
data class PhenologyScreenDataUi(
    val allergenName: String,
    val locationLabel: String,
    val currentStageLabel: String,
    val currentStageEpochSeconds: Long?,
    val stages: ImmutableList<PhenologyStageUi>,
)
