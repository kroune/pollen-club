package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.PhenologyDao
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.AddFenologyRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.PhenologyRepository
import io.github.kroune.pollen.domain.session.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PhenologyRepositoryImpl(
    private val api: PollenApiService,
    private val phenologyDao: PhenologyDao,
    private val session: UserSession,
) : PhenologyRepository {

    override fun observeObservations(): Flow<List<PhenologyObservationDomain>> {
        return phenologyDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun submitObservation(
        observation: PhenologyObservationDomain,
    ): ApiResult<Unit> = safeApiCall {
        api.addFenology(
            AddFenologyRequest(
                userId = session.requireUserId(),
                date = observation.date.toString(),
                time = observation.time,
                comment = observation.comment,
                state = observation.state,
                latitude = observation.latitude,
                longitude = observation.longitude,
            ),
        )
        phenologyDao.insert(observation.toEntity())
    }
}
