package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.HealthDao
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.AddUserFeelRequest
import io.github.kroune.pollen.data.remote.dto.request.AddUserSymptomsRequest
import io.github.kroune.pollen.data.remote.dto.request.SymptomEntryDto
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HealthRepositoryImpl(
    private val api: PollenApiService,
    private val healthDao: HealthDao,
) : HealthRepository {

    override suspend fun saveEntry(entry: HealthEntryDomain): Long {
        return healthDao.insert(entry.toEntity(0))
    }

    override fun observeEntries(): Flow<List<HealthEntryDomain>> {
        return healthDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getEntryByDate(date: String): HealthEntryDomain? {
        return healthDao.getByDate(date)?.toDomain()
    }

    override suspend fun syncToServer(userId: Long): ApiResult<Unit> = safeApiCall {
        val unsynced = healthDao.getUnsynced()
        for (entry in unsynced) {
            api.addUserFeel(
                AddUserFeelRequest(
                    date = entry.date,
                    location = entry.locationId,
                    time = entry.time,
                    opinion = entry.feeling,
                    opinionOld = entry.feeling,
                    latitude = entry.latitude,
                    longitude = entry.longitude,
                    userId = userId,
                    defaultPollen = entry.defaultPollen,
                    tags = entry.tags,
                    locationName = entry.locationName,
                ),
            )
            if (entry.eyes > 0 || entry.nose > 0 || entry.throat > 0 ||
                entry.lungs > 0 || entry.general > 0 || entry.other.isNotEmpty()
            ) {
                api.addUserSymptoms(
                    AddUserSymptomsRequest(
                        userId = userId,
                        symptoms = listOf(
                            SymptomEntryDto(
                                date = entry.date,
                                tags = entry.tags,
                                nose = entry.nose,
                                throat = entry.throat,
                                eyes = entry.eyes,
                                general = entry.general,
                                lungs = entry.lungs,
                                other = entry.other,
                            ),
                        ),
                    ),
                )
            }
            healthDao.markSynced(entry.id)
        }
    }
}
