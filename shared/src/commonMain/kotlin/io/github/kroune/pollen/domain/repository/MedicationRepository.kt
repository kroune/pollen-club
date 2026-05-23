package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.CureActionTypeDomain
import io.github.kroune.pollen.domain.model.CureDomain
import io.github.kroune.pollen.domain.model.CureDoseDomain
import io.github.kroune.pollen.domain.model.CureFormDomain
import io.github.kroune.pollen.domain.model.CureFrequencyDomain
import io.github.kroune.pollen.domain.model.MedicationIntakeDomain
import io.github.kroune.pollen.domain.model.TherapyDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface MedicationRepository {
    suspend fun getCureCatalog(): ApiResult<CureCatalog>
    fun observeTherapies(): Flow<List<TherapyDomain>>
    suspend fun saveTherapy(therapy: TherapyDomain, userId: Long): ApiResult<Unit>
    suspend fun deleteTherapy(therapy: TherapyDomain)
    suspend fun recordIntake(therapyId: Long, date: LocalDate, taken: Boolean)
    fun observeIntakesForDate(date: LocalDate): Flow<List<MedicationIntakeDomain>>
    fun observeAllTakenIntakes(): Flow<List<MedicationIntakeDomain>>
}

data class CureCatalog(
    val actionTypes: List<CureActionTypeDomain>,
    val cures: List<CureDomain>,
    val forms: List<CureFormDomain>,
    val doses: List<CureDoseDomain>,
    val frequencies: List<CureFrequencyDomain>,
)
