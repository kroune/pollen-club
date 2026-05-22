package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface PollenRepository {
    fun observePollens(): Flow<List<PollenDomain>>
    fun observeEnglishNames(): Flow<Map<Int, String>>
    suspend fun syncPollens(): ApiResult<Unit>
    suspend fun getLevelsForLocation(locationId: Int, date: LocalDate): List<LevelDomain>
    fun observeLevelsForLocation(locationId: Int): Flow<List<LevelDomain>>
    suspend fun syncLevels(): ApiResult<Unit>
    suspend fun getForecastsForLocation(locationId: Int, date: LocalDate): List<LevelDomain>
    fun observeForecastsForLocation(locationId: Int): Flow<List<LevelDomain>>
    suspend fun syncForecasts(): ApiResult<Unit>
    suspend fun getForecastTimeline(locationId: Int, pollenId: Int): List<LevelDomain>
    suspend fun getForecastTimeline(locationId: Int, pollenId: Int, startDate: LocalDate, endDate: LocalDate): List<LevelDomain>
    suspend fun resetSyncState()
}
