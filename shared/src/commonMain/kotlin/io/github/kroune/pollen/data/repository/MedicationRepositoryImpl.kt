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
import io.github.kroune.pollen.domain.session.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate

class MedicationRepositoryImpl(
    private val api: PollenApiService,
    private val therapyDao: TherapyDao,
    private val medicationIntakeDao: MedicationIntakeDao,
    private val localeProvider: LocaleProvider,
    private val session: UserSession,
) : MedicationRepository {

    private val cacheMutex = Mutex()
    private var cachedCatalog: CureCatalog? = null
    private var cacheTimestamp: Long = 0L

    override suspend fun getCureCatalog(): ApiResult<CureCatalog> {
        val now = kotlin.time.Clock.System.now().epochSeconds
        cacheMutex.withLock {
            cachedCatalog?.let { cached ->
                if (now - cacheTimestamp < CACHE_TTL_SECONDS) return ApiResult.Success(cached)
            }
        }
        return safeApiCall {
            val locale = localeProvider.current()
            val response = api.getCures()
            CureCatalog(
                actionTypes = response.actionTypes.map { it.toDomain(locale) },
                cures = response.cures.map { it.toDomain(locale) },
                forms = response.forms.map { it.toDomain(locale) },
                doses = response.doses.map { it.toDomain(locale) },
                frequencies = response.frequency.map { it.toDomain(locale) },
            ).also { catalog ->
                cacheMutex.withLock {
                    cachedCatalog = catalog
                    cacheTimestamp = kotlin.time.Clock.System.now().epochSeconds
                }
            }
        }
    }

    companion object {
        private const val CACHE_TTL_SECONDS = 3600L
    }

    override fun observeTherapies(): Flow<List<TherapyDomain>> =
        therapyDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveTherapy(therapy: TherapyDomain): ApiResult<Unit> = safeApiCall {
        api.addUserCure(
            AddUserCureRequest(
                userId = session.requireUserId(),
                date = therapy.date.toString(),
                cureId = therapy.cureId,
                cureName = therapy.cureName,
                formaId = 0,
                forma = therapy.form,
                frequencyId = 0,
                frequency = therapy.frequency,
                useFrom = therapy.startDate.toString(),
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

    override suspend fun recordIntake(therapyId: Long, date: LocalDate, taken: Boolean) {
        medicationIntakeDao.upsert(
            MedicationIntakeEntity(therapyId = therapyId, date = date, taken = taken),
        )
    }

    override fun observeIntakesForDate(date: LocalDate): Flow<List<MedicationIntakeDomain>> =
        medicationIntakeDao.observeByDate(date).map { list ->
            list.map { it.toDomain() }
        }

    override fun observeAllTakenIntakes(): Flow<List<MedicationIntakeDomain>> =
        medicationIntakeDao.observeAllTaken().map { list -> list.map { it.toDomain() } }
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
