package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.model.WeatherDomain
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel


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
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val personalIndexRepository: PersonalIndexRepository,
    private val sensitivityRepository: SensitivityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeSensitivities()
        loadData()
    }

    private fun observeSensitivities() {
        viewModelScope.launch {
            sensitivityRepository.observeAll().collect { sensitivities ->
                val sensitiveIds = sensitivities
                    .filter { it.level != SensitivityLevel.NONE }
                    .map { it.pollenId }
                    .toImmutableSet()
                _state.value = _state.value.copy(sensitivePollenIds = sensitiveIds)
                recomputeSensitivityDependents(sensitivities)
            }
        }
    }

    private suspend fun recomputeSensitivityDependents(sensitivities: List<AllergenSensitivityDomain>) {
        val pollens = _state.value.pollens.dataOrNull ?: return
        val location = _state.value.selectedLocation ?: return
        val levels = _state.value.levels.dataOrNull ?: return
        val hasSensitivities = sensitivities.any { it.level != SensitivityLevel.NONE }
        if (hasSensitivities) {
            loadDayForecasts(location.id, sensitivities)
            loadPersonalIndex(levels, sensitivities, pollens)
        } else {
            _state.value = _state.value.copy(
                dayForecasts = LoadState.Loading,
                personalIndex = LoadState.Loading,
            )
        }
    }

    fun loadData() {
        _state.value = HomeUiState(
            user = _state.value.user,
            showLocationPicker = _state.value.showLocationPicker,
        )

        viewModelScope.launch {
            var user = userRepository.getLocalUser()
            if (user == null || user.serverId == 0L) {
                val result = userRepository.registerOrUpdateUser(user ?: UserDomain())
                if (result is ApiResult.Success) {
                    user = userRepository.getLocalUser()
                }
            }
            _state.value = _state.value.copy(user = user)

            val pollensDeferred = async { loadPollens() }
            val locationsDeferred = async { loadLocations(user) }
            launch { loadWeather(locationsDeferred.await()) }
            launch {
                val pollens = pollensDeferred.await()
                val location = locationsDeferred.await()
                loadLevels(pollens, location)
                val sensitivities = sensitivityRepository.getAll()
                recomputeSensitivityDependents(sensitivities)
            }
        }
    }

    private suspend fun loadPollens(): ImmutableList<PollenDomain>? {
        return try {
            pollenRepository.syncPollens()
            pollenRepository.syncLevels()
            pollenRepository.syncForecasts()
            val pollens = pollenRepository.observePollens().first().toImmutableList()
            _state.value = _state.value.copy(pollens = LoadState.Loaded(pollens))
            pollens
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(pollens = LoadState.Failed)
            _events.send(UiEvent.ShowError(e.message ?: "Failed to load pollen data"))
            null
        }
    }

    private suspend fun loadLocations(user: UserDomain?): LocationDomain? {
        return try {
            locationRepository.syncLocations()
            val locations = locationRepository.getAll().toImmutableList()
            val selectedLocation = if (user != null && user.location > 0) {
                locationRepository.getById(user.location)
            } else {
                locations.firstOrNull()
            }
            _state.value = _state.value.copy(
                locations = LoadState.Loaded(locations),
                selectedLocation = selectedLocation,
            )
            selectedLocation
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(locations = LoadState.Failed)
            _events.send(UiEvent.ShowError(e.message ?: "Failed to load locations"))
            null
        }
    }

    private suspend fun loadWeather(selectedLocation: LocationDomain?) {
        if (selectedLocation == null) {
            _state.value = _state.value.copy(weather = LoadState.Failed)
            return
        }
        try {
            val result = weatherRepository.getCurrentWeather(
                selectedLocation.latitude, selectedLocation.longitude,
            )
            when (result) {
                is ApiResult.Success -> _state.value = _state.value.copy(weather = LoadState.Loaded(result.data))
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(weather = LoadState.Failed)
                    _events.send(UiEvent.ShowError(result.message))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(weather = LoadState.Failed)
            _events.send(UiEvent.ShowError(e.message ?: "Failed to load weather"))
        }
    }

    private suspend fun loadLevels(pollens: ImmutableList<PollenDomain>?, selectedLocation: LocationDomain?) {
        if (pollens == null || selectedLocation == null) {
            _state.value = _state.value.copy(levels = LoadState.Failed)
            return
        }
        try {
            val today = kotlin.time.Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            val live = pollenRepository.getLevelsForLocation(selectedLocation.id, today)
            val levels = live.ifEmpty {
                pollenRepository.getForecastsForLocation(selectedLocation.id, today)
            }.toImmutableList()
            _state.value = _state.value.copy(levels = LoadState.Loaded(levels))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(levels = LoadState.Failed)
            _events.send(UiEvent.ShowError(e.message ?: "Failed to load pollen levels"))
        }
    }

    fun selectLocation(location: LocationDomain) {
        _state.value = _state.value.copy(
            selectedLocation = location,
            showLocationPicker = false,
            levels = LoadState.Loading,
            weather = LoadState.Loading,
            dayForecasts = LoadState.Loading,
            personalIndex = LoadState.Loading,
            expandedPollenId = null,
            forecastTimeline = LoadState.Loading,
        )
        viewModelScope.launch {
            launch { loadWeather(location) }
            launch {
                val pollens = _state.value.pollens.dataOrNull
                loadLevels(pollens, location)
                val sensitivities = sensitivityRepository.getAll()
                recomputeSensitivityDependents(sensitivities)
            }
        }
    }

    fun showLocationPicker() {
        _state.value = _state.value.copy(showLocationPicker = true)
    }

    fun dismissLocationPicker() {
        _state.value = _state.value.copy(showLocationPicker = false)
    }

    fun selectDay(index: Int) {
        val forecasts = _state.value.dayForecasts.dataOrNull ?: return
        if (index < 0 || index >= forecasts.size) return
        _state.value = _state.value.copy(
            activeDayIndex = index,
            levels = LoadState.Loading,
            personalIndex = LoadState.Loading,
        )
        viewModelScope.launch {
            val location = _state.value.selectedLocation ?: return@launch
            val selectedDate = forecasts[index].date
            loadLevelsForDate(location.id, selectedDate)
            val sensitivities = sensitivityRepository.getAll()
            val levels = _state.value.levels.dataOrNull ?: return@launch
            val pollens = _state.value.pollens.dataOrNull ?: return@launch
            loadPersonalIndex(levels, sensitivities, pollens)
        }
    }

    private suspend fun loadLevelsForDate(locationId: Int, date: String) {
        try {
            val live = pollenRepository.getLevelsForLocation(locationId, date)
            val levels = live.ifEmpty {
                pollenRepository.getForecastsForLocation(locationId, date)
            }.toImmutableList()
            _state.value = _state.value.copy(levels = LoadState.Loaded(levels))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(levels = LoadState.Failed)
            _events.send(UiEvent.ShowError("Не удалось загрузить данные за этот день"))
        }
    }

    fun addAllergen(pollenId: Int) {
        viewModelScope.launch {
            try {
                sensitivityRepository.setSensitivity(pollenId, SensitivityLevel.LIGHT)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(UiEvent.ShowError("Не удалось добавить аллерген"))
            }
        }
    }

    fun toggleAllergenExpanded(pollenId: Int) {
        val current = _state.value.expandedPollenId
        if (current == pollenId) {
            _state.value = _state.value.copy(
                expandedPollenId = null,
                forecastTimeline = LoadState.Loading,
            )
        } else {
            _state.value = _state.value.copy(
                expandedPollenId = pollenId,
                forecastTimeline = LoadState.Loading,
            )
            viewModelScope.launch { loadForecastTimeline(pollenId) }
        }
    }


    private suspend fun loadDayForecasts(
        locationId: Int,
        sensitivities: List<AllergenSensitivityDomain>,
    ) {
        try {
            val summaries = personalIndexRepository.computeDayForecastSummaries(
                locationId, sensitivities,
            )
            val dayOfWeekNames = listOf("пн", "вт", "ср", "чт", "пт", "сб", "вс")
            val dayForecasts = summaries.toUi(dayOfWeekNames)
            _state.value = _state.value.copy(
                dayForecasts = LoadState.Loaded(dayForecasts),
                activeDayIndex = 0,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _state.value = _state.value.copy(dayForecasts = LoadState.Failed)
        }
    }

    private suspend fun loadPersonalIndex(
        levels: ImmutableList<LevelDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        pollens: ImmutableList<PollenDomain>,
    ) {
        try {
            val index = personalIndexRepository.computePersonalIndex(
                levels, sensitivities, pollens,
            )
            val severityLabels = listOf("Нулевой", "Низкий", "Средний", "Высокий", "Очень высокий", "Экстра")
            _state.value = _state.value.copy(
                personalIndex = LoadState.Loaded(index.toUi(severityLabels)),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _state.value = _state.value.copy(personalIndex = LoadState.Failed)
        }
    }

    private suspend fun loadForecastTimeline(pollenId: Int) {
        val locationId = _state.value.selectedLocation?.id ?: return
        try {
            val timeline = pollenRepository.getForecastTimeline(locationId, pollenId)
                .toImmutableList()
            _state.value = _state.value.copy(forecastTimeline = LoadState.Loaded(timeline))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(forecastTimeline = LoadState.Failed)
            _events.send(UiEvent.ShowError("Failed to load forecast"))
        }
    }
}
