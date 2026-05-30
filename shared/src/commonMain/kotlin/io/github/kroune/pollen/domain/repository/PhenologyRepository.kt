package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import kotlinx.coroutines.flow.Flow

interface PhenologyRepository {
    fun observeObservations(): Flow<List<PhenologyObservationDomain>>
    suspend fun submitObservation(observation: PhenologyObservationDomain): ApiResult<Unit>
}
