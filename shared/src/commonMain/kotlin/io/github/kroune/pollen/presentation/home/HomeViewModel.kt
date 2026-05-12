package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@Stable
data class HomeUiState(
    val user: UserDomain? = null,
    val selectedLocation: LocationDomain? = null,
    val locations: LoadState<ImmutableList<LocationDomain>> = LoadState.Loading,
    val pollens: LoadState<ImmutableList<PollenDomain>> = LoadState.Loading,
    val levels: LoadState<ImmutableList<LevelDomain>> = LoadState.Loading,
    val weather: LoadState<WeatherDomain> = LoadState.Loading,
    val dayForecasts: LoadState<ImmutableList<HomeDayForecastUi>> = LoadState.Loading,
    val personalIndex: LoadState<HomePersonalIndexUi> = LoadState.Loading,
    val sensitivePollenIds: ImmutableSet<Int> = persistentSetOf(),
    val activeDayIndex: Int = 0,
    val showLocationPicker: Boolean = false,
    val expandedPollenId: Int? = null,
    val forecastTimeline: LoadState<ImmutableList<LevelDomain>> = LoadState.Loading,
    val today: LocalDate? = null,
)

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
    private val _expandedPollenId = MutableStateFlow<Int?>(null)
    private val _forecastTimeline = MutableStateFlow<LoadState<ImmutableList<LevelDomain>>>(LoadState.Loading)
    private val _weather = MutableStateFlow<LoadState<WeatherDomain>>(LoadState.Loading)
    private val _dayForecasts = MutableStateFlow<LoadState<ImmutableList<HomeDayForecastUi>>>(LoadState.Loading)
    private val _personalIndex = MutableStateFlow<LoadState<HomePersonalIndexUi>>(LoadState.Loading)

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
        sensitivityRepository.observeAll(),
        locationRepository.observeLocations().map { it.toImmutableList() },
        userRepository.observeUser(),
        todayProvider.today,
    ) { pollens, sensitivities, locations, user, today ->
        CoreData(pollens, sensitivities, locations, user, today)
    }.combine(selectedLocation) { core, location ->
        Pair(core, location)
    }.combine(levelsFlow) { (core, location), levels ->
        Triple(core, location, levels)
    }.combine(
        combine(_weather, _dayForecasts, _personalIndex, _activeDayIndex, _showLocationPicker) { w, d, p, ai, slp ->
            UiExtras(w, d, p, ai, slp)
        },
    ) { (core, location, levels), extras ->
        val sensitiveIds = core.sensitivities
            .filter { it.level != SensitivityLevel.NONE }
            .map { it.pollenId }
            .toImmutableSet()

        HomeUiState(
            user = core.user,
            selectedLocation = location,
            locations = LoadState.Loaded(core.locations),
            pollens = LoadState.Loaded(core.pollens),
            levels = if (levels.isEmpty() && location != null) LoadState.Loading else LoadState.Loaded(levels),
            weather = extras.weather,
            dayForecasts = extras.dayForecasts,
            personalIndex = extras.personalIndex,
            sensitivePollenIds = sensitiveIds,
            activeDayIndex = extras.activeDayIndex,
            showLocationPicker = extras.showLocationPicker,
            expandedPollenId = _expandedPollenId.value,
            forecastTimeline = _forecastTimeline.value,
            today = core.today,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        loadData()
        observeLocationChanges()
        observeLevelChanges()
    }

    fun loadData() {
        viewModelScope.launch {
            var user = userRepository.getLocalUser()
            if (user == null || user.serverId == 0L) {
                val result = userRepository.registerOrUpdateUser(user ?: UserDomain())
                if (result is ApiResult.Success) {
                    user = userRepository.getLocalUser()
                }
            }
        }
        viewModelScope.launch {
            try { pollenRepository.syncPollens() } catch (e: CancellationException) { throw e } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { pollenRepository.syncLevels() } catch (e: CancellationException) { throw e } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { pollenRepository.syncForecasts() } catch (e: CancellationException) { throw e } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { locationRepository.syncLocations() } catch (e: CancellationException) { throw e } catch (_: Exception) {}
        }
    }

    private fun observeLocationChanges() {
        viewModelScope.launch {
            selectedLocation.collect { location ->
                if (location != null) {
                    _weather.value = LoadState.Loading
                    _dayForecasts.value = LoadState.Loading
                    loadWeather(location)
                    val sensitivities = sensitivityRepository.getAll()
                    if (sensitivities.any { it.level != SensitivityLevel.NONE }) {
                        loadDayForecasts(location.id, sensitivities)
                    }
                }
            }
        }
    }

    private fun observeLevelChanges() {
        viewModelScope.launch {
            combine(levelsFlow, sensitivityRepository.observeAll(), pollenRepository.observePollens()) { levels, sens, pollens ->
                Triple(levels, sens, pollens)
            }.collect { (levels, sensitivities, pollens) ->
                if (levels.isNotEmpty() && sensitivities.any { it.level != SensitivityLevel.NONE }) {
                    loadPersonalIndex(levels, sensitivities, pollens.toImmutableList())
                } else {
                    _personalIndex.value = LoadState.Loading
                }
            }
        }
    }

    fun selectLocation(location: LocationDomain) {
        _selectedLocationOverride.value = location
        _showLocationPicker.value = false
        _activeDayIndex.value = 0
        _expandedPollenId.value = null
        _forecastTimeline.value = LoadState.Loading
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
                val sensitivities = sensitivityRepository.getAll()
                val pollens = state.value.pollens.dataOrNull ?: return@launch
                loadPersonalIndex(levels, sensitivities, pollens)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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
        }
    }

    private suspend fun loadDayForecasts(
        locationId: Int,
        sensitivities: List<AllergenSensitivityDomain>,
    ) {
        try {
            val summaries = personalIndexRepository.computeDayForecastSummaries(locationId, sensitivities)
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
            _activeDayIndex.value = 0
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _dayForecasts.value = LoadState.Failed
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
        } catch (_: Exception) {
            _personalIndex.value = LoadState.Failed
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
    val sensitivities: List<AllergenSensitivityDomain>,
    val locations: ImmutableList<LocationDomain>,
    val user: UserDomain?,
    val today: LocalDate,
)

private data class UiExtras(
    val weather: LoadState<WeatherDomain>,
    val dayForecasts: LoadState<ImmutableList<HomeDayForecastUi>>,
    val personalIndex: LoadState<HomePersonalIndexUi>,
    val activeDayIndex: Int,
    val showLocationPicker: Boolean,
)
