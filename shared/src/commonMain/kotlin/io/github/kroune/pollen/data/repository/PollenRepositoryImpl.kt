package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.LevelDao
import io.github.kroune.pollen.data.local.db.dao.PollenDao
import io.github.kroune.pollen.data.local.db.dao.SyncStateDao
import io.github.kroune.pollen.data.local.db.entity.SyncStateEntity
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.mapper.toForecastEntity
import io.github.kroune.pollen.data.mapper.toLevelEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.LevelForecastRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.PollenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

private const val STALE_DATA_DAYS = 60

class PollenRepositoryImpl(
    private val api: PollenApiService,
    private val pollenDao: PollenDao,
    private val levelDao: LevelDao,
    private val syncStateDao: SyncStateDao,
    private val localeProvider: LocaleProvider,
) : PollenRepository {

    override fun observePollens(): Flow<List<PollenDomain>> =
        combine(pollenDao.observeAll(), localeProvider.currentLocale) { entities, locale ->
            entities.map { entity ->
                val levelInfos = pollenDao.getLevelInfos(entity.id)
                entity.toDomain(locale, levelInfos)
            }
        }

    override fun observeEnglishNames(): Flow<Map<Int, String>> =
        pollenDao.observeAll().map { entities ->
            entities.associate { it.id to it.descEng }
        }

    override suspend fun syncPollens(): ApiResult<Unit> = safeApiCall {
        val response = api.getPollens()
        val pollenEntities = response.pollens.map { it.toEntity() }
        if (pollenEntities.isEmpty()) return@safeApiCall
        val levelInfoEntities = response.pollens.flatMap { dto ->
            dto.levels.map { level -> level.toEntity(dto.id) }
        }
        pollenDao.replacePollensAndLevels(pollenEntities, levelInfoEntities)
    }

    override suspend fun getLevelsForLocation(locationId: Int, date: String): List<LevelDomain> =
        levelDao.getByLocationAndDate(locationId, date).map { it.toDomain() }

    override fun observeLevelsForLocation(locationId: Int): Flow<List<LevelDomain>> =
        levelDao.observeByLocation(locationId).map { list -> list.map { it.toDomain() } }

    override suspend fun syncLevels(): ApiResult<Unit> = safeApiCall {
        val state = syncStateDao.getSyncState() ?: SyncStateEntity()
        val response = api.getLevels(state.lastLevelsId)
        val entities = response.levels.orEmpty().map { it.toLevelEntity() }
        if (entities.isNotEmpty()) {
            levelDao.upsertLevels(entities)
            syncStateDao.upsert(state.copy(lastLevelsId = entities.maxOf { it.id }))
        }
        val cutoff = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .minus(DatePeriod(days = STALE_DATA_DAYS))
            .toString()
        levelDao.deleteLevelsOlderThan(cutoff)
    }

    override suspend fun getForecastsForLocation(locationId: Int, date: String): List<LevelDomain> =
        levelDao.getForecastsByLocationAndDate(locationId, date).map { it.toDomain() }

    override fun observeForecastsForLocation(locationId: Int): Flow<List<LevelDomain>> =
        levelDao.observeForecastsByLocation(locationId).map { list -> list.map { it.toDomain() } }

    override suspend fun syncForecasts(): ApiResult<Unit> = safeApiCall {
        val state = syncStateDao.getSyncState() ?: SyncStateEntity()
        val response = api.getForecasts(LevelForecastRequest(fromId = state.lastForecastId))
        val entities = response.levels.orEmpty().map { it.toForecastEntity() }
        if (entities.isNotEmpty()) {
            levelDao.upsertForecasts(entities)
            syncStateDao.upsert(state.copy(lastForecastId = entities.maxOf { it.id }))
        }
        val cutoff = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .minus(DatePeriod(days = STALE_DATA_DAYS))
            .toString()
        levelDao.deleteForecastsOlderThan(cutoff)
    }

    override suspend fun getForecastTimeline(locationId: Int, pollenId: Int): List<LevelDomain> {
        val now = kotlin.time.Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return getForecastTimeline(
            locationId, pollenId,
            today.minus(DatePeriod(days = 3)).toString(),
            today.plus(DatePeriod(days = 7)).toString(),
        )
    }

    override suspend fun resetSyncState() {
        syncStateDao.resetLevelAndForecastIds()
    }

    override suspend fun getForecastTimeline(
        locationId: Int,
        pollenId: Int,
        startDate: String,
        endDate: String,
    ): List<LevelDomain> {
        val observed = levelDao.getLevelsByLocationAndPollen(locationId, pollenId)
            .map { it.toDomain() }
            .filter { it.date in startDate..endDate }
        val forecasts = levelDao.getForecastsByLocationAndPollen(locationId, pollenId)
            .map { it.toDomain() }
            .filter { it.date in startDate..endDate }
        val observedDates = observed.map { it.date }.toSet()
        val merged = observed + forecasts.filter { it.date !in observedDates }
        return merged.sortedBy { it.date }
    }
}
