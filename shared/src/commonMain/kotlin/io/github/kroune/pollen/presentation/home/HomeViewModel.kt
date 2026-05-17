package io.github.kroune.pollen.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LevelDomain
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.model.WeatherDomain
import io.github.kroune.pollen.util.normalizeSeverity
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import dev.icerock.moko.resources.desc.RawStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.plus
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.diary.monthShortStringDesc
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private val logger = Logger.withTag("HomeViewModel")
private const val DAYS_PER_WEEK = 7

class HomeViewModel(
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val personalIndexRepository: PersonalIndexRepository,
    private val sensitivityRepository: SensitivityRepository,
    private val todayProvider: TodayProvider,
) : ViewModel() {

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _selectedLocationOverride = MutableStateFlow<LocationDomain?>(null)
    private val _activeDayIndex = MutableStateFlow(0)
    private val _showLocationPicker = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _weekOffset = MutableStateFlow(0)
    private val _weekLabel = MutableStateFlow<StringDesc>(RawStringDesc(""))
    private val _expandedPollenId = MutableStateFlow<Int?>(null)
    private val _forecastTimeline = MutableStateFlow<LoadState<ImmutableList<LevelDomain>>>(LoadState.Loading)
    private val _selectedDayLevels = MutableStateFlow<ImmutableList<LevelDomain>?>(null)
    private val _weather = MutableStateFlow<LoadState<WeatherDomain>>(LoadState.Loading)
    private val _dayForecasts = MutableStateFlow<LoadState<ImmutableList<HomeDayForecastUi>>>(LoadState.Loading)
    private val _personalIndex = MutableStateFlow<LoadState<HomePersonalIndexUi?>>(LoadState.Loading)

    private val selectedLocation = combine(
        _selectedLocationOverride,
        userRepository.observeUser(),
        locationRepository.observeLocations(),
    ) { override, user, locations ->
        override
            ?: locations.firstOrNull { it.id == user?.location?.takeIf { id -> id > 0 } }
            ?: locations.firstOrNull()
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val levelsFlow = combine(
        selectedLocation,
        todayProvider.today,
    ) { location, today -> Pair(location, today) }
        .flatMapLatest { (location, today) ->
            if (location == null) return@flatMapLatest flowOf(emptyList<LevelDomain>().toImmutableList())
            combine(
                pollenRepository.observeLevelsForLocation(location.id),
                pollenRepository.observeForecastsForLocation(location.id),
            ) { levels, forecasts ->
                val todayStr = today.toString()
                val todayLevels = levels.filter { it.date == todayStr }
                val result = todayLevels.ifEmpty {
                    forecasts.filter { it.date == todayStr }
                }
                result.toImmutableList()
            }
        }

    val state: StateFlow<HomeUiState> = combine(
        pollenRepository.observePollens().map { it.toImmutableList() },
        sensitivityRepository.observeAll().map { it.toImmutableList() },
        locationRepository.observeLocations().map { it.toImmutableList() },
        userRepository.observeUser(),
        todayProvider.today,
    ) { pollens, sensitivities, locations, user, today ->
        CoreData(pollens, sensitivities, locations, user, today)
    }.combine(selectedLocation) { core, location ->
        Pair(core, location)
    }.combine(levelsFlow) { (core, location), levels ->
        Triple(core, location, levels)
    }.combine(_selectedDayLevels) { (core, location, todayLevels), selectedLevels ->
        Triple(core, location, selectedLevels ?: todayLevels)
    }.combine(
        combine(_weather, _dayForecasts, _personalIndex, _activeDayIndex, _showLocationPicker) { w, d, p, ai, slp ->
            UiExtras(w, d, p, ai, slp)
        },
    ) { (core, location, levels), extras ->
        val sensitiveIds = core.sensitivities
            .filter { it.level != SensitivityLevel.NONE }
            .map { it.pollenId }
            .toImmutableSet()

        val levelsMap = levels.associateBy { it.pollenId }
        val hasSelection = sensitiveIds.isNotEmpty()

        val userAllergens = if (hasSelection) {
            core.pollens.filter { it.id in sensitiveIds }.map { pollen ->
                val rawValue = levelsMap[pollen.id]?.value ?: 0
                AllergenRowData(pollen, normalizeSeverity(rawValue, pollen.maxLevel), pollen.maxLevel)
            }.toImmutableList()
        } else {
            emptyList<AllergenRowData>().toImmutableList()
        }

        val otherAllergens = if (hasSelection) {
            core.pollens.filter { it.id !in sensitiveIds }.toImmutableList()
        } else {
            core.pollens.toImmutableList()
        }

        HomeUiState(
            user = core.user,
            selectedLocation = location,
            locations = LoadState.Loaded(core.locations),
            pollens = LoadState.Loaded(core.pollens),
            weather = extras.weather,
            dayForecasts = extras.dayForecasts,
            personalIndex = extras.personalIndex,
            userAllergens = userAllergens,
            otherAllergens = otherAllergens,
            activeDayIndex = extras.activeDayIndex,
            showLocationPicker = extras.showLocationPicker,
            today = core.today,
        )
    }.combine(_expandedPollenId) { state, expandedId ->
        state.copy(expandedPollenId = expandedId)
    }.combine(_forecastTimeline) { state, timeline ->
        state.copy(forecastTimeline = timeline)
    }.combine(_isRefreshing) { state, refreshing ->
        state.copy(
            isRefreshing = refreshing,
            pollens = if (refreshing && state.pollens.dataOrNull?.isEmpty() == true)
                LoadState.Loading else state.pollens,
            locations = if (refreshing && state.locations.dataOrNull?.isEmpty() == true)
                LoadState.Loading else state.locations,
        )
    }.combine(
        combine(_weekOffset, _weekLabel) { offset, label -> Pair(offset, label) },
    ) { state, (offset, label) ->
        state.copy(weekOffset = offset, weekLabel = label)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        syncData(forceRefresh = false)
        observeLocationChanges()
        observeLevelChanges()
    }

    fun loadData() {
        syncData(forceRefresh = true)
    }

    private fun syncData(forceRefresh: Boolean) {
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                if (forceRefresh) {
                    try {
                        pollenRepository.resetSyncState()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        logger.w(e) { "Failed to reset sync state" }
                    }
                }
                var syncFailures = 0
                coroutineScope {
                    launch {
                        try {
                            var user = userRepository.getLocalUser()
                            if (user == null || user.serverId == 0L) {
                                val result = userRepository.registerOrUpdateUser(user ?: UserDomain())
                                if (result !is ApiResult.Success) {
                                    logger.w { "User registration returned error" }
                                }
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            logger.w(e) { "Failed to register/update user" }
                        }
                    }
                    launch {
                        try {
                            pollenRepository.syncPollens()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            syncFailures++
                            logger.w(e) { "Failed to sync pollens" }
                        }
                    }
                    launch {
                        try {
                            pollenRepository.syncLevels()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            syncFailures++
                            logger.w(e) { "Failed to sync levels" }
                        }
                    }
                    launch {
                        try {
                            pollenRepository.syncForecasts()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            syncFailures++
                            logger.w(e) { "Failed to sync forecasts" }
                        }
                    }
                    launch {
                        try {
                            locationRepository.syncLocations()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            syncFailures++
                            logger.w(e) { "Failed to sync locations" }
                        }
                    }
                }
                val location = selectedLocation.first()
                if (location != null) {
                    loadDayForecasts(location.id)
                }
                if (syncFailures > 0) {
                    _events.send(UiEvent.ShowError(MR.strings.error_load_data.desc()))
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun observeLocationChanges() {
        viewModelScope.launch {
            selectedLocation.collect { location ->
                if (location != null) {
                    _weather.value = LoadState.Loading
                    _dayForecasts.value = LoadState.Loading
                    _selectedDayLevels.value = null
                    launch { loadWeather(location) }
                    if (!_isRefreshing.value) {
                        loadDayForecasts(location.id)
                    }
                }
            }
        }
    }

    private fun observeLevelChanges() {
        viewModelScope.launch {
            combine(
                levelsFlow,
                _selectedDayLevels,
                sensitivityRepository.observeAll(),
                pollenRepository.observePollens(),
            ) { todayLevels, selectedLevels, sens, pollens ->
                Triple(selectedLevels ?: todayLevels, sens, pollens)
            }.collect { (levels, sensitivities, pollens) ->
                val hasActiveSensitivities = sensitivities.any { it.level != SensitivityLevel.NONE }
                if (levels.isNotEmpty() && hasActiveSensitivities) {
                    loadPersonalIndex(levels, sensitivities, pollens.toImmutableList())
                } else {
                    _personalIndex.value = LoadState.Loaded(null)
                }
            }
        }
    }

    fun selectLocation(location: LocationDomain) {
        _selectedLocationOverride.value = location
        _showLocationPicker.value = false
        _activeDayIndex.value = 0
        _weekOffset.value = 0
        _expandedPollenId.value = null
        _forecastTimeline.value = LoadState.Loading
        _selectedDayLevels.value = null
    }

    fun showLocationPicker() {
        _showLocationPicker.value = true
    }

    fun dismissLocationPicker() {
        _showLocationPicker.value = false
    }

    fun selectDay(index: Int) {
        val forecasts = _dayForecasts.value.dataOrNull ?: return
        if (index < 0 || index >= forecasts.size) return
        _activeDayIndex.value = index
        _personalIndex.value = LoadState.Loading

        viewModelScope.launch {
            val location = state.value.selectedLocation ?: return@launch
            val selectedDate = forecasts[index].date
            try {
                val live = pollenRepository.getLevelsForLocation(location.id, selectedDate)
                val levels = live.ifEmpty {
                    pollenRepository.getForecastsForLocation(location.id, selectedDate)
                }.toImmutableList()
                _selectedDayLevels.value = levels
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _personalIndex.value = LoadState.Failed
                _events.send(UiEvent.ShowError(MR.strings.error_load_day_data.desc()))
            }
        }
    }

    fun addAllergen(pollenId: Int) {
        viewModelScope.launch {
            try {
                sensitivityRepository.setSensitivity(pollenId, SensitivityLevel.LIGHT)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_add_allergen.desc()))
            }
        }
    }

    fun toggleAllergenExpanded(pollenId: Int) {
        val current = _expandedPollenId.value
        if (current == pollenId) {
            _expandedPollenId.value = null
            _forecastTimeline.value = LoadState.Loading
        } else {
            _expandedPollenId.value = pollenId
            _forecastTimeline.value = LoadState.Loading
            viewModelScope.launch { loadForecastTimeline(pollenId) }
        }
    }

    private suspend fun loadWeather(location: LocationDomain) {
        try {
            when (val result = weatherRepository.getCurrentWeather(location.latitude, location.longitude)) {
                is ApiResult.Success -> _weather.value = LoadState.Loaded(result.data)
                is ApiResult.Error -> {
                    _weather.value = LoadState.Failed
                    _events.send(UiEvent.ShowError(MR.strings.error_load_weather.desc()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _weather.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_weather.desc()))
        }
    }

    fun shiftWeek(delta: Int) {
        val newOffset = _weekOffset.value + delta
        _weekOffset.value = newOffset
        _activeDayIndex.value = 0
        _dayForecasts.value = LoadState.Loading
        _selectedDayLevels.value = null
        _personalIndex.value = LoadState.Loading

        viewModelScope.launch {
            val location = state.value.selectedLocation ?: return@launch
            loadDayForecasts(location.id)
        }
    }

    private fun computeWeekStartDate(weekOffset: Int): LocalDate {
        val today = todayProvider.today.value
        val todayDow = today.dayOfWeek.ordinal
        val monday = today.minus(todayDow.toLong(), DateTimeUnit.DAY)
        return monday.plus((weekOffset * 7).toLong(), DateTimeUnit.DAY)
    }

    private fun computeWeekLabel(startDate: LocalDate): StringDesc {
        val endDate = startDate.plus(6, DateTimeUnit.DAY)
        val startMonth = monthShortStringDesc(startDate.month)
        val endMonth = monthShortStringDesc(endDate.month)
        return if (startDate.month == endDate.month) {
            "${startDate.day}–${endDate.day} ".desc() + startMonth
        } else {
            "${startDate.day} ".desc() + startMonth + " – ${endDate.day} ".desc() + endMonth
        }
    }

    private suspend fun loadDayForecasts(locationId: Int) {
        try {
            val weekStart = computeWeekStartDate(_weekOffset.value)
            _weekLabel.value = computeWeekLabel(weekStart)

            val summaries = personalIndexRepository.computeDayForecastSummariesForAllPollens(
                locationId, weekStart, DAYS_PER_WEEK,
            )
            val dayOfWeekNames = listOf(
                MR.strings.dow_mon.desc(),
                MR.strings.dow_tue.desc(),
                MR.strings.dow_wed.desc(),
                MR.strings.dow_thu.desc(),
                MR.strings.dow_fri.desc(),
                MR.strings.dow_sat.desc(),
                MR.strings.dow_sun.desc(),
            )
            val dayForecasts = summaries.toUi(dayOfWeekNames)
            _dayForecasts.value = LoadState.Loaded(dayForecasts)
            _activeDayIndex.value = if (_weekOffset.value == 0) {
                val today = todayProvider.today.value
                dayForecasts.indexOfFirst { it.date == today.toString() }.coerceAtLeast(0)
            } else {
                0
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _dayForecasts.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
        }
    }

    private suspend fun loadPersonalIndex(
        levels: ImmutableList<LevelDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        pollens: ImmutableList<PollenDomain>,
    ) {
        try {
            val index = personalIndexRepository.computePersonalIndex(levels, sensitivities, pollens)
            val severityLabels = listOf(
                MR.strings.severity_none.desc(),
                MR.strings.severity_low.desc(),
                MR.strings.severity_medium.desc(),
                MR.strings.severity_high.desc(),
                MR.strings.severity_very_high.desc(),
                MR.strings.severity_extra.desc(),
            )
            _personalIndex.value = LoadState.Loaded(index.toUi(severityLabels))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _personalIndex.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_data.desc()))
        }
    }

    private suspend fun loadForecastTimeline(pollenId: Int) {
        val locationId = state.value.selectedLocation?.id ?: return
        try {
            val timeline = pollenRepository.getForecastTimeline(locationId, pollenId).toImmutableList()
            _forecastTimeline.value = LoadState.Loaded(timeline)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _forecastTimeline.value = LoadState.Failed
            _events.send(UiEvent.ShowError(MR.strings.error_load_forecast.desc()))
        }
    }
}

private data class CoreData(
    val pollens: ImmutableList<PollenDomain>,
    val sensitivities: ImmutableList<AllergenSensitivityDomain>,
    val locations: ImmutableList<LocationDomain>,
    val user: UserDomain?,
    val today: LocalDate,
)

private data class UiExtras(
    val weather: LoadState<WeatherDomain>,
    val dayForecasts: LoadState<ImmutableList<HomeDayForecastUi>>,
    val personalIndex: LoadState<HomePersonalIndexUi?>,
    val activeDayIndex: Int,
    val showLocationPicker: Boolean,
)
