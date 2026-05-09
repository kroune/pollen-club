package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.StatisticsDao
import io.github.kroune.pollen.data.local.db.dao.SyncStateDao
import io.github.kroune.pollen.data.local.db.entity.SyncStateEntity
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.GetStatisticsRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.StatisticDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StatisticsRepositoryImpl(
    private val api: PollenApiService,
    private val statisticsDao: StatisticsDao,
    private val syncStateDao: SyncStateDao,
) : StatisticsRepository {

    override fun observeByLocation(locationId: Int): Flow<List<StatisticDomain>> {
        return statisticsDao.observeByLocation(locationId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncStatistics(): ApiResult<Unit> = safeApiCall {
        val state = syncStateDao.getSyncState() ?: SyncStateEntity()
        val fromId = state.lastStatisticsId
        val response = api.getStatistics(GetStatisticsRequest(fromId = fromId))
        val entities = response.statistics.map { it.toEntity() }
        if (entities.isNotEmpty()) {
            statisticsDao.upsertAll(entities)
            val maxId = entities.maxOf { it.id }.toLong()
            syncStateDao.upsert(state.copy(lastStatisticsId = maxId))
        }
    }
}
