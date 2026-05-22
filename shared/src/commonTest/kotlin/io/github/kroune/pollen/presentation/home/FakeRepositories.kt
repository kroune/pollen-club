package io.github.kroune.pollen.presentation.home

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.model.WeatherDomain
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class FakeUserRepository : UserRepository {
    val userFlow = MutableStateFlow<UserDomain?>(null)
    var localUser: UserDomain? = null

    override fun observeUser(): Flow<UserDomain?> = userFlow
    override suspend fun getLocalUser(): UserDomain? = localUser ?: userFlow.value
    override suspend fun registerOrUpdateUser(user: UserDomain): ApiResult<Long> {
        userFlow.value = user.copy(serverId = 1L)
        localUser = userFlow.value
        return ApiResult.Success(1L)
    }

    override suspend fun updateAllergens(allergenIds: List<Int>) {}
    override suspend fun updateLocation(locationId: Int) {}
}

class FakePollenRepository : PollenRepository {
    val pollensFlow = MutableStateFlow<List<PollenDomain>>(emptyList())
    private val levelsFlow = MutableStateFlow<List<LevelDomain>>(emptyList())
    private val forecastsFlow = MutableStateFlow<List<LevelDomain>>(emptyList())

    var syncGate: CompletableDeferred<Unit>? = null
    var syncPollensError: Exception? = null
    var syncLevelsError: Exception? = null
    var syncForecastsError: Exception? = null
    val levelsForDate = mutableMapOf<Pair<Int, LocalDate>, List<LevelDomain>>()
    val forecastsForDate = mutableMapOf<Pair<Int, LocalDate>, List<LevelDomain>>()
    val forecastTimelines = mutableMapOf<Pair<Int, Int>, List<LevelDomain>>()

    fun emitLevels(levels: List<LevelDomain>) { levelsFlow.value = levels }
    fun emitForecasts(forecasts: List<LevelDomain>) { forecastsFlow.value = forecasts }

    override fun observePollens(): Flow<List<PollenDomain>> = pollensFlow
    override fun observeEnglishNames(): Flow<Map<Int, String>> =
        pollensFlow.map { list -> list.associate { it.id to it.name } }

    override fun observeLevelsForLocation(locationId: Int): Flow<List<LevelDomain>> =
        levelsFlow.map { list -> list.filter { it.locationId == locationId } }

    override fun observeForecastsForLocation(locationId: Int): Flow<List<LevelDomain>> =
        forecastsFlow.map { list -> list.filter { it.locationId == locationId } }

    override suspend fun syncPollens(): ApiResult<Unit> {
        syncGate?.await()
        syncPollensError?.let { throw it }
        return ApiResult.Success(Unit)
    }

    override suspend fun syncLevels(): ApiResult<Unit> {
        syncLevelsError?.let { throw it }
        return ApiResult.Success(Unit)
    }

    override suspend fun syncForecasts(): ApiResult<Unit> {
        syncForecastsError?.let { throw it }
        return ApiResult.Success(Unit)
    }

    override suspend fun resetSyncState() {}

    override suspend fun getLevelsForLocation(locationId: Int, date: LocalDate): List<LevelDomain> =
        levelsForDate[locationId to date]
            ?: levelsFlow.value.filter { it.locationId == locationId && it.date == date }

    override suspend fun getForecastsForLocation(locationId: Int, date: LocalDate): List<LevelDomain> =
        forecastsForDate[locationId to date]
            ?: forecastsFlow.value.filter { it.locationId == locationId && it.date == date }

    override suspend fun getForecastTimeline(locationId: Int, pollenId: Int): List<LevelDomain> =
        forecastTimelines[locationId to pollenId] ?: emptyList()

    override suspend fun getForecastTimeline(
        locationId: Int,
        pollenId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<LevelDomain> =
        forecastTimelines[locationId to pollenId]?.filter { it.date in startDate..endDate } ?: emptyList()
}

class FakeLocationRepository : LocationRepository {
    val locationsFlow = MutableStateFlow<List<LocationDomain>>(emptyList())
    var syncError: Exception? = null

    override fun observeLocations(): Flow<List<LocationDomain>> = locationsFlow
    override suspend fun getAll(): List<LocationDomain> = locationsFlow.value
    override suspend fun getById(id: Int): LocationDomain? = locationsFlow.value.firstOrNull { it.id == id }
    override suspend fun syncLocations(): ApiResult<Unit> {
        syncError?.let { throw it }
        return ApiResult.Success(Unit)
    }
}

class FakeWeatherRepository : WeatherRepository {
    var result: ApiResult<WeatherDomain> = ApiResult.Success(
        WeatherDomain(temperature = 22.0, weatherCode = 0, isDay = true),
    )

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): ApiResult<WeatherDomain> = result
}

class FakePersonalIndexRepository : PersonalIndexRepository {
    var indexResult = PersonalPollenIndexDomain(value = 3.5, maxPossible = 10.0, dominantAllergenName = "Birch")
    var dayForecastSummaries: List<DayForecastSummaryDomain> = emptyList()

    override suspend fun computePersonalIndex(
        levels: List<LevelDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        pollens: List<PollenDomain>,
    ): PersonalPollenIndexDomain = indexResult

    override suspend fun computeDayForecastSummaries(
        locationId: Int,
        sensitivities: List<AllergenSensitivityDomain>,
        days: Int,
    ): List<DayForecastSummaryDomain> = dayForecastSummaries

    override suspend fun computeDayForecastSummaries(
        locationId: Int,
        sensitivities: List<AllergenSensitivityDomain>,
        startDate: LocalDate,
        days: Int,
    ): List<DayForecastSummaryDomain> = dayForecastSummaries

    override suspend fun computeDayForecastSummariesForAllPollens(
        locationId: Int,
        startDate: LocalDate,
        days: Int,
    ): List<DayForecastSummaryDomain> = dayForecastSummaries
}

class FakeSensitivityRepository : SensitivityRepository {
    private val sensitivitiesFlow = MutableStateFlow<List<AllergenSensitivityDomain>>(emptyList())
    var setSensitivityError: Exception? = null

    fun emit(sensitivities: List<AllergenSensitivityDomain>) {
        sensitivitiesFlow.value = sensitivities
    }

    override fun observeAll(): Flow<List<AllergenSensitivityDomain>> = sensitivitiesFlow

    override suspend fun getAll(): List<AllergenSensitivityDomain> = sensitivitiesFlow.value

    override suspend fun setSensitivity(pollenId: Int, level: SensitivityLevel) {
        setSensitivityError?.let { throw it }
        val current = sensitivitiesFlow.value.toMutableList()
        current.removeAll { it.pollenId == pollenId }
        if (level != SensitivityLevel.NONE) {
            current.add(AllergenSensitivityDomain(pollenId, level))
        }
        sensitivitiesFlow.value = current
    }

    override suspend fun setSensitivities(sensitivities: List<AllergenSensitivityDomain>) {
        sensitivitiesFlow.value = sensitivities
    }
}

class FakeTodayProvider(date: LocalDate) : TodayProvider {
    private val todayFlow = MutableStateFlow(date)
    override val today: StateFlow<LocalDate> = todayFlow

    fun setDate(date: LocalDate) { todayFlow.value = date }
}
