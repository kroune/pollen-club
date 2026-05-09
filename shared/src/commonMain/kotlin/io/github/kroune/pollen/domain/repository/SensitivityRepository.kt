package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import kotlinx.coroutines.flow.Flow

interface SensitivityRepository {
    fun observeAll(): Flow<List<AllergenSensitivityDomain>>
    suspend fun getAll(): List<AllergenSensitivityDomain>
    suspend fun setSensitivity(pollenId: Int, level: SensitivityLevel)
    suspend fun setSensitivities(sensitivities: List<AllergenSensitivityDomain>)
}
