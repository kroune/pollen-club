package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.AllergenSensitivityDao
import io.github.kroune.pollen.data.local.db.entity.AllergenSensitivityEntity
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SensitivityRepositoryImpl(
    private val dao: AllergenSensitivityDao,
) : SensitivityRepository {

    override fun observeAll(): Flow<List<AllergenSensitivityDomain>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<AllergenSensitivityDomain> =
        dao.getAll().map { it.toDomain() }

    override suspend fun setSensitivity(pollenId: Int, level: SensitivityLevel) {
        dao.upsert(AllergenSensitivityEntity(pollenId = pollenId, sensitivityLevel = level.value))
    }

    override suspend fun setSensitivities(sensitivities: List<AllergenSensitivityDomain>) {
        dao.upsertAll(sensitivities.map { it.toEntity() })
    }
}
