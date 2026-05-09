package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.detail.DetailStatsUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs


@Stable
data class ForecastDetailUiState(
    val pollen: LoadState<PollenDomain> = LoadState.Loading,
    val timeline: LoadState<ImmutableList<LevelDomain>> = LoadState.Loading,
    val today: LocalDate = kotlin.time.Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    // TODO: replace mocks with real data from repository
    val currentScore: String = "5,2",
    val currentScoreMax: String = "10",
    val severityLevel: Int = 2,
    val severityLabel: String = "Средний",
    val stats: DetailStatsUi? = DetailStatsUi(
        peakDate = "24 апр",
        declineDate = "~30 апр",
        symptomCount = 2,
    ),
    val aboutText: String = "Активная фаза. Обычно пыление берёзы длится около 3 недель. Уточняется на основании данных пыльцеуловителей и фенологии.",
    val showFeelingLine: Boolean = true,
    // TODO: replace with real feeling data from diary/health repository
    val feelingValues: ImmutableList<Int?> = persistentListOf(),
)

class ForecastDetailViewModel(
    private val pollenId: Int,
    private val pollenRepository: PollenRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ForecastDetailUiState())
    val state: StateFlow<ForecastDetailUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                val pollens = pollenRepository.observePollens().first()
                val pollen = pollens.firstOrNull { it.id == pollenId }
                if (pollen == null) {
                    _state.value = _state.value.copy(pollen = LoadState.Failed)
                    _events.send(UiEvent.ShowError("Allergen not found"))
                    return@launch
                }
                _state.value = _state.value.copy(pollen = LoadState.Loaded(pollen))

                val user = userRepository.getLocalUser()
                val locationId = if (user != null && user.location > 0) {
                    user.location
                } else {
                    locationRepository.getAll().firstOrNull()?.id
                }

                if (locationId != null) {
                    loadTimeline(locationId)
                } else {
                    _state.value = _state.value.copy(timeline = LoadState.Failed)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(pollen = LoadState.Failed)
                _events.send(UiEvent.ShowError("Failed to load data"))
            }
        }
    }

    fun toggleFeelingLine() {
        _state.value = _state.value.copy(showFeelingLine = !_state.value.showFeelingLine)
    }

    private suspend fun loadTimeline(locationId: Int) {
        val endDate = _state.value.today.plus(DatePeriod(days = 5)).toString()
        try {
            val timeline = pollenRepository.getForecastTimeline(locationId, pollenId, "2000-01-01", endDate)
                .toImmutableList()
            // TODO: replace with real feeling data from diary/health repository
            val mockFeeling = generateMockFeelingData(timeline.size)
            _state.value = _state.value.copy(
                timeline = LoadState.Loaded(timeline),
                feelingValues = mockFeeling,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(timeline = LoadState.Failed)
            _events.send(UiEvent.ShowError("Failed to load forecast"))
        }
    }

    // TODO: replace with real feeling data from diary/health repository
    private fun generateMockFeelingData(size: Int): ImmutableList<Int?> {
        if (size < 5) return persistentListOf()
        val center = size * 2 / 3
        val radius = size / 4
        return List(size) { i ->
            val dist = abs(i - center)
            if (dist <= radius) (3 - dist * 3 / radius).coerceIn(0, 3)
            else null
        }.toImmutableList()
    }
}
