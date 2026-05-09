package io.github.kroune.pollen.presentation.phenology

import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import io.github.kroune.pollen.domain.model.PhenologyStageRegistry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun mapPhenologyStages(
    observations: List<PhenologyObservationDomain>,
    locale: AppLocale,
): ImmutableList<PhenologyStageUi> {
    val allStages = PhenologyStageRegistry.getStages(locale)
    val maxObservedState = observations.maxOfOrNull { it.state }
    // Observations use 0-indexed state (0–5), stages use 1-indexed numbers (1–6)
    val currentStageNumber = if (maxObservedState != null) maxObservedState + 1 else 0

    return allStages.map { stage ->
        PhenologyStageUi(
            number = stage.number,
            name = stage.name,
            description = stage.description,
            isDone = stage.number < currentStageNumber,
            isCurrent = stage.number == currentStageNumber,
        )
    }.toImmutableList()
}
