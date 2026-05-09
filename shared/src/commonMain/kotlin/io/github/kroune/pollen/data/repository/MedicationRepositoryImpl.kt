package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.MedicationIntakeDao
import io.github.kroune.pollen.data.local.db.dao.TherapyDao
import io.github.kroune.pollen.data.local.db.entity.MedicationIntakeEntity
import io.github.kroune.pollen.data.local.db.entity.TherapyEntity
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.AddUserCureRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.MedicationIntakeDomain
import io.github.kroune.pollen.domain.model.TherapyDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.CureCatalog
import io.github.kroune.pollen.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicationRepositoryImpl(
    private val api: PollenApiService,
    private val therapyDao: TherapyDao,
    private val medicationIntakeDao: MedicationIntakeDao,
    private val localeProvider: LocaleProvider,
) : MedicationRepository {

    override suspend fun getCureCatalog(): ApiResult<CureCatalog> = safeApiCall {
        val locale = localeProvider.current()
        val response = api.getCures()
        CureCatalog(
            actionTypes = response.actionTypes.map { it.toDomain(locale) },
            cures = response.cures.map { it.toDomain(locale) },
            forms = response.forms.map { it.toDomain(locale) },
            doses = response.doses.map { it.toDomain(locale) },
            frequencies = response.frequency.map { it.toDomain(locale) },
        )
    }

    override fun observeTherapies(): Flow<List<TherapyDomain>> =
        therapyDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveTherapy(therapy: TherapyDomain, userId: Long): ApiResult<Unit> = safeApiCall {
        api.addUserCure(
            AddUserCureRequest(
                userId = userId,
                date = therapy.date,
                cureId = therapy.cureId,
                cureName = therapy.cureName,
                formaId = 0,
                forma = therapy.form,
                frequencyId = 0,
                frequency = therapy.frequency,
                useFrom = therapy.startDate,
                doseId = 0,
                dose = therapy.dose,
                doseValue = 0,
                frequencyValue = 0,
                type = therapy.cureTypeId,
            ),
        )
        therapyDao.insert(therapy.toEntity())
    }

    override suspend fun deleteTherapy(therapy: TherapyDomain) {
        therapyDao.delete(therapy.toEntity())
    }

    override suspend fun recordIntake(therapyId: Long, date: String, taken: Boolean) {
        medicationIntakeDao.upsert(
            MedicationIntakeEntity(therapyId = therapyId, date = date, taken = taken),
        )
    }

    override fun observeIntakesForDate(date: String): Flow<List<MedicationIntakeDomain>> =
        medicationIntakeDao.observeByDate(date).map { list ->
            list.map { it.toDomain() }
        }

    private fun MedicationIntakeEntity.toDomain() = MedicationIntakeDomain(
        id = id,
        therapyId = therapyId,
        date = date,
        taken = taken,
    )
}

private fun TherapyDomain.toEntity() = TherapyEntity(
    id = id,
    date = date,
    cureTypeId = cureTypeId,
    cureName = cureName,
    cureId = cureId,
    form = form,
    dose = dose,
    frequency = frequency,
    startDate = startDate,
)
