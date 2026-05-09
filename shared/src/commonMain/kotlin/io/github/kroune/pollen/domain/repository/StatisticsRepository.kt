package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.StatisticDomain
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun observeByLocation(locationId: Int): Flow<List<StatisticDomain>>
    suspend fun syncStatistics(): ApiResult<Unit>
}
