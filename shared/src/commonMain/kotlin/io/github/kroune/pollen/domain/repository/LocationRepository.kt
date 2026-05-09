package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LocationDomain
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun observeLocations(): Flow<List<LocationDomain>>
    suspend fun getAll(): List<LocationDomain>
    suspend fun getById(id: Int): LocationDomain?
    suspend fun syncLocations(): ApiResult<Unit>
}
