package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.LocationDao
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepositoryImpl(
    private val api: PollenApiService,
    private val locationDao: LocationDao,
    private val localeProvider: LocaleProvider,
) : LocationRepository {

    override fun observeLocations(): Flow<List<LocationDomain>> =
        locationDao.observeAll().map { entities ->
            val locale = localeProvider.current()
            entities.map { it.toDomain(locale) }
        }

    override suspend fun getAll(): List<LocationDomain> =
        locationDao.getAll().map { it.toDomain(localeProvider.current()) }

    override suspend fun getById(id: Int): LocationDomain? =
        locationDao.getById(id)?.toDomain(localeProvider.current())

    override suspend fun syncLocations(): ApiResult<Unit> = safeApiCall {
        locationDao.upsertAll(api.getLocations().locations.map { it.toEntity() })
    }
}
