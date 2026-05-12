package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.detail.DetailStatsUi
import org.jetbrains.compose.resources.DrawableResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.cancellation.CancellationException

@Stable
data class ForecastDetailUiState(
    val pollen: LoadState<PollenDomain> = LoadState.Loading,
    val timeline: LoadState<ImmutableList<LevelDomain>> = LoadState.Loading,
    val today: LocalDate = kotlin.time.Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val currentScore: String = "—",
    val currentScoreMax: String = "10",
    val severityLevel: Int = 0,
    val severityLabel: String = "",
    val stats: DetailStatsUi? = null,
    val aboutText: String = "",
    val pollenIconRes: DrawableResource? = null,
    val showFeelingLine: Boolean = true,
    val feelingValues: ImmutableList<Int?> = persistentListOf(),
)

class ForecastDetailViewModel(
    private val pollenId: Int,
    private val pollenRepository: PollenRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val healthRepository: HealthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ForecastDetailUiState())
    val state: StateFlow<ForecastDetailUiState> = _state

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
                    _events.send(UiEvent.ShowError(MR.strings.error_allergen_not_found.desc()))
                    return@launch
                }
                _state.value = _state.value.copy(
                    pollen = LoadState.Loaded(pollen),
                    aboutText = pollen.description,
                    pollenIconRes = PollenIconRegistry.iconFor(pollen.id),
                )

                val user = userRepository.getLocalUser()
                val locationId = if (user != null && user.location > 0) {
                    user.location
                } else {
                    locationRepository.getAll().firstOrNull()?.id
                }

                if (locationId != null) {
                    loadTimeline(locationId, pollen)
                } else {
                    _state.value = _state.value.copy(timeline = LoadState.Failed)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(pollen = LoadState.Failed)
                _events.send(UiEvent.ShowError(MR.strings.error_load_data.desc()))
            }
        }
    }

    fun toggleFeelingLine() {
        _state.value = _state.value.copy(showFeelingLine = !_state.value.showFeelingLine)
    }

    private suspend fun loadTimeline(locationId: Int, pollen: PollenDomain) {
        val today = _state.value.today
        val endDate = today.plus(DatePeriod(days = 5)).toString()
        try {
            val timeline = pollenRepository.getForecastTimeline(locationId, pollenId, "2000-01-01", endDate)
                .toImmutableList()

            val todayLevel = timeline.firstOrNull { it.date == today.toString() }
            val maxLevel = pollen.levels.maxOfOrNull { it.level } ?: 10
            val currentValue = todayLevel?.value ?: 0
            val scoreRatio = if (maxLevel > 0) currentValue.toFloat() / maxLevel * 10 else 0f
            val wholepart = scoreRatio.toInt()
            val fractpart = ((scoreRatio - wholepart) * 10).toInt()
            val scoreFormatted = "$wholepart,$fractpart"

            val severityLevel = computeSeverityLevel(currentValue, maxLevel)
            val severityLabel = pollen.levels
                .firstOrNull { it.level == currentValue }?.name ?: ""

            val healthEntries = healthRepository.observeEntries().first()
            val feelingValues = mapFeelingValues(timeline, healthEntries)

            val stats = computeStats(timeline, healthEntries, today)

            _state.value = _state.value.copy(
                timeline = LoadState.Loaded(timeline),
                currentScore = scoreFormatted,
                currentScoreMax = "10",
                severityLevel = severityLevel,
                severityLabel = severityLabel,
                stats = stats,
                feelingValues = feelingValues,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(timeline = LoadState.Failed)
            _events.send(UiEvent.ShowError(MR.strings.error_load_forecast.desc()))
        }
    }

    private fun computeSeverityLevel(value: Int, maxLevel: Int): Int {
        if (maxLevel <= 0) return 0
        return ((value.toFloat() / maxLevel) * 5).toInt().coerceIn(0, 5)
    }

    private fun mapFeelingValues(
        timeline: List<LevelDomain>,
        healthEntries: List<HealthEntryDomain>,
    ): ImmutableList<Int?> {
        val entryMap = healthEntries.associateBy { it.date }
        return timeline.map { level ->
            val entry = entryMap[level.date]
            entry?.let { feelingToInt(it.feeling) }
        }.toImmutableList()
    }

    private fun feelingToInt(feeling: Feeling): Int = when (feeling) {
        Feeling.GOOD -> 0
        Feeling.MIDDLE -> 1
        Feeling.BAD -> 2
    }

    private fun computeStats(
        timeline: List<LevelDomain>,
        healthEntries: List<HealthEntryDomain>,
        today: LocalDate,
    ): DetailStatsUi? {
        if (timeline.isEmpty()) return null

        val peak = timeline.maxByOrNull { it.value } ?: return null

        val peakIndex = timeline.indexOf(peak)
        val decline = timeline.drop(peakIndex + 1).firstOrNull { it.value < peak.value }

        val timelineDates = timeline.map { it.date }.toSet()
        val symptomCount = healthEntries.count { it.date in timelineDates && it.feeling == Feeling.BAD }

        return DetailStatsUi(
            peakDate = peak.date,
            declineDate = decline?.date,
            symptomCount = symptomCount,
        )
    }
}
