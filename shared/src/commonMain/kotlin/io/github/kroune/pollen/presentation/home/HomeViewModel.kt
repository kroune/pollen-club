package io.github.kroune.pollen.presentation.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.model.UserAllergenProfile
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.domain.usecase.DayForecastSummaryUseCase
import io.github.kroune.pollen.domain.usecase.ObserveUserAllergensUseCase
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.startOfWeek
import io.github.kroune.pollen.presentation.diary.monthShortStringDesc
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
private val EMPTY_PROFILE = UserAllergenProfile(
    allergens = emptyList(),
    otherPollens = emptyList(),
    index = null,
)

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
    private val observeUserAllergensUseCase: ObserveUserAllergensUseCase,
    private val dayForecastSummaryUseCase: DayForecastSummaryUseCase,
    private val sensitivityRepository: SensitivityRepository,
    private val todayProvider: TodayProvider,
) : MviViewModel<HomeUiState, HomeIntent, UiEvent>(HomeUiState()) {

    private val _selectedLocationIdOverride = MutableStateFlow<Int?>(null)
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)

    private val selectedLocationFlow = combine(
        _selectedLocationIdOverride,
        userSession.user,
        locationRepository.observeLocations(),
    ) { overrideId, user, locations ->
        overrideId?.let { id -> locations.firstOrNull { it.id == id } }
            ?: locations.firstOrNull { it.id == user.location }
            ?: locations.firstOrNull()
    }.distinctUntilChanged()

    init {
        observePollens()
        observeLocations()
        observeToday()
        observeSelectedLocation()
        observeUserAllergens()
        observeLocationChanges()
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

    /**
     * Single reactive source for the user's allergens, the non-sensitive complement and the
     * personal index. Re-subscribes whenever the location or the selected day changes; otherwise
     * the use case pushes updates on its own as sensitivities or levels change.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUserAllergens() {
        viewModelScope.launch {
            combine(
                selectedLocationFlow,
                todayProvider.today,
                _selectedDate,
            ) { location, today, selectedDate ->
                location?.let { AllergenQuery(locationId = it.id, date = selectedDate ?: today) }
            }.distinctUntilChanged().flatMapLatest { query ->
                if (query == null) flowOf(EMPTY_PROFILE)
                else observeUserAllergensUseCase(query.locationId, query.date)
            }.collect { profile ->
                updateState {
                    copy(
                        userAllergens = profile.allergens
                            .map { it.toRowData() }
                            .toImmutableList(),
                        otherAllergens = profile.otherPollens
                            .map { HomeOtherAllergenUi(it.id, it.name) }
                            .toImmutableList(),
                        personalIndex = LoadState.Loaded(profile.index?.toUi()),
                    )
                }
            }
        }
    }

    private data class AllergenQuery(val locationId: Int, val date: LocalDate)

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
                    _selectedDate.value = null
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

    private fun selectLocation(locationId: Int) {
        _selectedLocationIdOverride.value = locationId
        _selectedDate.value = null
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
        // The allergen observer reacts to _selectedDate and re-emits the profile for the new day.
        updateState { copy(activeDayIndex = index, personalIndex = LoadState.Loading) }
        _selectedDate.value = forecasts[index].date
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
        _selectedDate.value = null

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

            val summaries = dayForecastSummaryUseCase(locationId, weekStart, DAYS_PER_WEEK)
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
