package io.github.kroune.pollen.presentation.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.startOfWeek
import io.github.kroune.pollen.presentation.diary.monthShortStringDesc
import io.github.kroune.pollen.util.normalizeSeverity
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private val logger = Logger.withTag("HomeViewModel")
private const val DAYS_PER_WEEK = 7

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data class SelectLocation(val locationId: Int) : HomeIntent
    data object ShowLocationPicker : HomeIntent
    data object DismissLocationPicker : HomeIntent
    data class SelectDay(val index: Int) : HomeIntent
    data class ShiftWeek(val delta: Int) : HomeIntent
    data class AddAllergen(val pollenId: Int) : HomeIntent
    data class ToggleAllergenExpanded(val pollenId: Int) : HomeIntent
}

class HomeViewModel(
    private val userSession: UserSession,
    private val pollenRepository: PollenRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val personalIndexRepository: PersonalIndexRepository,
    private val sensitivityRepository: SensitivityRepository,
    private val todayProvider: TodayProvider,
) : MviViewModel<HomeUiState, HomeIntent, UiEvent>(HomeUiState()) {

    private val _selectedLocationIdOverride = MutableStateFlow<Int?>(null)
    private val _selectedDayLevels = MutableStateFlow<List<LevelDomain>?>(null)

    private val selectedLocationFlow = combine(
        _selectedLocationIdOverride,
        userSession.user,
        locationRepository.observeLocations(),
    ) { overrideId, user, locations ->
        overrideId?.let { id -> locations.firstOrNull { it.id == id } }
            ?: locations.firstOrNull { it.id == user.location }
            ?: locations.firstOrNull()
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val todayLevelsFlow: Flow<List<LevelDomain>> = combine(
        selectedLocationFlow,
        todayProvider.today,
    ) { location, today -> location to today }
        .flatMapLatest { (location, today) ->
            if (location == null) return@flatMapLatest flowOf(emptyList())
            combine(
                pollenRepository.observeLevelsForLocation(location.id),
                pollenRepository.observeForecastsForLocation(location.id),
            ) { levels, forecasts ->
                val todayLevels = levels.filter { it.date == today }
                todayLevels.ifEmpty { forecasts.filter { it.date == today } }
            }
        }

    init {
        observePollens()
        observeLocations()
        observeToday()
        observeSelectedLocation()
        observeUserAllergens()
        observeOtherAllergens()
        observeLocationChanges()
        observePersonalIndexOnLevels()
        syncData(forceRefresh = false)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData -> syncData(forceRefresh = true)
            is HomeIntent.SelectLocation -> selectLocation(intent.locationId)
            HomeIntent.ShowLocationPicker -> updateState { copy(showLocationPicker = true) }
            HomeIntent.DismissLocationPicker -> updateState { copy(showLocationPicker = false) }
            is HomeIntent.SelectDay -> selectDay(intent.index)
            is HomeIntent.ShiftWeek -> shiftWeek(intent.delta)
            is HomeIntent.AddAllergen -> addAllergen(intent.pollenId)
            is HomeIntent.ToggleAllergenExpanded -> toggleAllergenExpanded(intent.pollenId)
        }
    }

    private fun observePollens() {
        viewModelScope.launch {
            pollenRepository.observePollens().collect { pollens ->
                updateState { copy(pollens = LoadState.Loaded(pollens.toImmutableList())) }
            }
        }
    }

    private fun observeLocations() {
        viewModelScope.launch {
            locationRepository.observeLocations().collect { locations ->
                updateState {
                    copy(
                        locations = LoadState.Loaded(
                            locations.map { HomeLocationUi(it.id, it.name) }.toImmutableList(),
                        ),
                    )
                }
            }
        }
    }

    private fun observeToday() {
        viewModelScope.launch {
            todayProvider.today.collect { today ->
                updateState { copy(today = today) }
            }
        }
    }

    private fun observeSelectedLocation() {
        viewModelScope.launch {
            selectedLocationFlow.collect { location ->
                updateState {
                    copy(selectedLocation = location?.let { HomeLocationUi(it.id, it.name) })
                }
            }
        }
    }

    private fun observeUserAllergens() {
        viewModelScope.launch {
            combine(
                pollenRepository.observePollens(),
                sensitivityRepository.observeAll(),
                todayLevelsFlow,
                _selectedDayLevels,
            ) { pollens, sensitivities, todayLevels, selectedDayLevels ->
                computeUserAllergens(pollens, sensitivities, selectedDayLevels ?: todayLevels)
                    .toImmutableList()
            }.collect { rows ->
                updateState { copy(userAllergens = rows) }
            }
        }
    }

    private fun observeOtherAllergens() {
        viewModelScope.launch {
            combine(
                pollenRepository.observePollens(),
                sensitivityRepository.observeAll(),
            ) { pollens, sensitivities ->
                val sensitiveIds = sensitivities
                    .filter { it.level != SensitivityLevel.NONE }
                    .map { it.pollenId }
                    .toSet()
                val filtered = if (sensitiveIds.isEmpty()) pollens
                else pollens.filter { it.id !in sensitiveIds }
                filtered.map { HomeOtherAllergenUi(it.id, it.name) }
            }.collect { others ->
                updateState { copy(otherAllergens = others.toImmutableList()) }
            }
        }
    }

    private fun observeLocationChanges() {
        viewModelScope.launch {
            selectedLocationFlow.collect { location ->
                if (location != null) {
                    updateState {
                        copy(
                            weather = LoadState.Loading,
                            dayForecasts = LoadState.Loading,
                        )
                    }
                    _selectedDayLevels.value = null
                    launch { loadWeather(location) }
                    // Avoid duplicate forecast loads if a sync is already in progress;
                    // syncData will call loadDayForecasts after it finishes.
                    if (!currentState.isRefreshing) {
                        loadDayForecasts(location.id)
                    }
                }
            }
        }
    }

    private fun observePersonalIndexOnLevels() {
        viewModelScope.launch {
            combine(
                todayLevelsFlow,
                _selectedDayLevels,
                sensitivityRepository.observeAll(),
                pollenRepository.observePollens(),
            ) { todayLevels, selectedDayLevels, sensitivities, pollens ->
                PersonalIndexInputs(selectedDayLevels ?: todayLevels, sensitivities, pollens)
            }.collect { inputs ->
                val hasActiveSensitivities = inputs.sensitivities.any { it.level != SensitivityLevel.NONE }
                if (inputs.levels.isNotEmpty() && hasActiveSensitivities) {
                    loadPersonalIndex(inputs.levels, inputs.sensitivities, inputs.pollens)
                } else {
                    updateState { copy(personalIndex = LoadState.Loaded(null)) }
                }
            }
        }
    }

    private data class PersonalIndexInputs(
        val levels: List<LevelDomain>,
        val sensitivities: List<AllergenSensitivityDomain>,
        val pollens: List<PollenDomain>,
    )

    private fun computeUserAllergens(
        pollens: List<PollenDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        levels: List<LevelDomain>,
    ): List<AllergenRowData> {
        val sensitiveIds = sensitivities
            .filter { it.level != SensitivityLevel.NONE }
            .map { it.pollenId }
            .toSet()
        if (sensitiveIds.isEmpty()) return emptyList()
        val levelsMap = levels.associateBy { it.pollenId }
        return pollens.filter { it.id in sensitiveIds }.map { pollen ->
            val rawValue = levelsMap[pollen.id]?.value ?: 0
            AllergenRowData(pollen, normalizeSeverity(rawValue, pollen.maxLevel), pollen.maxLevel)
        }
    }

    private fun selectLocation(locationId: Int) {
        _selectedLocationIdOverride.value = locationId
        _selectedDayLevels.value = null
        updateState {
            copy(
                showLocationPicker = false,
                activeDayIndex = 0,
                weekOffset = 0,
                expandedPollenId = null,
                forecastTimeline = LoadState.Loading,
            )
        }
    }

    private fun selectDay(index: Int) {
        val forecasts = currentState.dayForecasts.dataOrNull ?: return
        if (index < 0 || index >= forecasts.size) return
        updateState { copy(activeDayIndex = index, personalIndex = LoadState.Loading) }

        viewModelScope.launch {
            val location = currentState.selectedLocation ?: return@launch
            val selectedDate = forecasts[index].date
            runCatchingCancellable {
                val live = pollenRepository.getLevelsForLocation(location.id, selectedDate)
                _selectedDayLevels.value = live.ifEmpty {
                    pollenRepository.getForecastsForLocation(location.id, selectedDate)
                }
            }.onFailure {
                updateState { copy(personalIndex = LoadState.Failed) }
                emitEffect(UiEvent.ShowError(MR.strings.error_load_day_data.desc()))
            }
        }
    }

    private fun shiftWeek(delta: Int) {
        val newOffset = currentState.weekOffset + delta
        updateState {
            copy(
                weekOffset = newOffset,
                activeDayIndex = 0,
                dayForecasts = LoadState.Loading,
                personalIndex = LoadState.Loading,
            )
        }
        _selectedDayLevels.value = null

        viewModelScope.launch {
            val location = currentState.selectedLocation ?: return@launch
            loadDayForecasts(location.id)
        }
    }

    private fun addAllergen(pollenId: Int) {
        viewModelScope.launch {
            runCatchingCancellable {
                sensitivityRepository.setSensitivity(pollenId, SensitivityLevel.LIGHT)
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_add_allergen.desc()))
            }
        }
    }

    private fun toggleAllergenExpanded(pollenId: Int) {
        if (currentState.expandedPollenId == pollenId) {
            updateState { copy(expandedPollenId = null, forecastTimeline = LoadState.Loading) }
        } else {
            updateState { copy(expandedPollenId = pollenId, forecastTimeline = LoadState.Loading) }
            viewModelScope.launch { loadForecastTimeline(pollenId) }
        }
    }

    private fun syncData(forceRefresh: Boolean) {
        // Show loading indicators for empty pollens/locations while refreshing
        // to avoid showing an empty state during initial load.
        updateState {
            copy(
                isRefreshing = true,
                pollens = if (pollens.dataOrNull?.isEmpty() == true) LoadState.Loading else pollens,
                locations = if (locations.dataOrNull?.isEmpty() == true) LoadState.Loading else locations,
            )
        }
        viewModelScope.launch {
            try {
                if (forceRefresh) {
                    runCatchingCancellable { pollenRepository.resetSyncState() }
                        .onFailure { logger.w(it) { "Failed to reset sync state" } }
                }
                val anyFailure = coroutineScope {
                    listOf(
                        async { runCatchingCancellable { pollenRepository.syncPollens() } },
                        async { runCatchingCancellable { pollenRepository.syncLevels() } },
                        async { runCatchingCancellable { pollenRepository.syncForecasts() } },
                        async { runCatchingCancellable { locationRepository.syncLocations() } },
                    ).awaitAll().any { it.isFailure }
                }
                // Load day forecasts after sync so the UI has data immediately.
                val location = currentState.selectedLocation
                if (location != null) {
                    loadDayForecasts(location.id)
                }
                if (anyFailure) {
                    emitEffect(UiEvent.ShowError(MR.strings.error_load_data.desc()))
                }
            } finally {
                updateState { copy(isRefreshing = false) }
            }
        }
    }

    private suspend fun loadWeather(location: LocationDomain) {
        when (val result = weatherRepository.getCurrentWeather(location.latitude, location.longitude)) {
            is ApiResult.Success -> {
                val w = result.data
                updateState {
                    copy(weather = LoadState.Loaded(HomeWeatherUi(w.temperature, w.weatherCode, w.isDay)))
                }
            }
            is ApiResult.Error -> {
                updateState { copy(weather = LoadState.Failed) }
                emitEffect(UiEvent.ShowError(MR.strings.error_load_weather.desc()))
            }
        }
    }

    private suspend fun loadDayForecasts(locationId: Int) {
        runCatchingCancellable {
            val weekStart = computeWeekStartDate(currentState.weekOffset)
            val weekLabel = computeWeekLabel(weekStart)
            updateState { copy(weekLabel = weekLabel) }

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
            val activeIndex = if (currentState.weekOffset == 0) {
                val today = todayProvider.today.value
                dayForecasts.indexOfFirst { it.date == today }.coerceAtLeast(0)
            } else {
                0
            }
            updateState {
                copy(
                    dayForecasts = LoadState.Loaded(dayForecasts),
                    activeDayIndex = activeIndex,
                )
            }
        }.onFailure {
            updateState { copy(dayForecasts = LoadState.Failed) }
            emitEffect(UiEvent.ShowError(MR.strings.error_load_pollen_forecast.desc()))
        }
    }

    private suspend fun loadPersonalIndex(
        levels: List<LevelDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        pollens: List<PollenDomain>,
    ) {
        runCatchingCancellable {
            val index = personalIndexRepository.computePersonalIndex(
                levels.toImmutableList(),
                sensitivities,
                pollens.toImmutableList(),
            )
            val severityLabels = listOf(
                MR.strings.severity_none.desc(),
                MR.strings.severity_low.desc(),
                MR.strings.severity_medium.desc(),
                MR.strings.severity_high.desc(),
                MR.strings.severity_very_high.desc(),
                MR.strings.severity_extra.desc(),
            )
            updateState { copy(personalIndex = LoadState.Loaded(index.toUi(severityLabels))) }
        }.onFailure {
            updateState { copy(personalIndex = LoadState.Failed) }
            emitEffect(UiEvent.ShowError(MR.strings.error_load_data.desc()))
        }
    }

    private suspend fun loadForecastTimeline(pollenId: Int) {
        val locationId = currentState.selectedLocation?.id ?: return
        runCatchingCancellable {
            val timeline = pollenRepository.getForecastTimeline(locationId, pollenId).toImmutableList()
            updateState { copy(forecastTimeline = LoadState.Loaded(timeline)) }
        }.onFailure {
            updateState { copy(forecastTimeline = LoadState.Failed) }
            emitEffect(UiEvent.ShowError(MR.strings.error_load_forecast.desc()))
        }
    }

    private fun computeWeekStartDate(weekOffset: Int): LocalDate {
        val monday = todayProvider.today.value.startOfWeek()
        return monday.plus((weekOffset * DAYS_PER_WEEK).toLong(), DateTimeUnit.DAY)
    }

    private fun computeWeekLabel(startDate: LocalDate): StringDesc {
        val endDate = startDate.plus(6, DateTimeUnit.DAY)
        val startMonth = monthShortStringDesc(startDate.month)
        val endMonth = monthShortStringDesc(endDate.month)
        return if (startDate.month == endDate.month) {
            StringDesc.ResourceFormatted(
                MR.strings.week_range_same_month,
                startDate.day, endDate.day, startMonth,
            )
        } else {
            StringDesc.ResourceFormatted(
                MR.strings.week_range_cross_month,
                startDate.day, startMonth, endDate.day, endMonth,
            )
        }
    }
}
