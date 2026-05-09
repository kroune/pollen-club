package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    suspend fun saveEntry(entry: HealthEntryDomain): Long
    fun observeEntries(): Flow<List<HealthEntryDomain>>
    suspend fun getEntryByDate(date: String): HealthEntryDomain?
    suspend fun syncToServer(userId: Long): ApiResult<Unit>
}
