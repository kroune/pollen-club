package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HealthRepository {
    suspend fun saveEntry(entry: HealthEntryDomain): Long
    fun observeEntries(): Flow<List<HealthEntryDomain>>
    suspend fun getEntryByDate(date: LocalDate): HealthEntryDomain?
    fun observeEntryByDate(date: LocalDate): Flow<HealthEntryDomain?>
    suspend fun syncToServer(userId: Long): ApiResult<Unit>
}
