package io.github.kroune.pollen.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.detail.DetailStatsUi
import io.github.kroune.pollen.util.normalizeSeverity
import kotlinx.collections.immutable.ImmutableList
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
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.coroutines.cancellation.CancellationException

private const val TIMELINE_LOOKBACK_DAYS = 30
private const val TIMELINE_LOOKAHEAD_DAYS = 5
private const val DEFAULT_MAX_LEVEL = 10

class ForecastDetailViewModel(
    private val pollenId: Int,
    private val pollenRepository: PollenRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val healthRepository: HealthRepository,
    private val todayProvider: TodayProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ForecastDetailUiState(today = todayProvider.today.value),
    )
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
                    _events.send(UiEvent.ShowError(MR.strings.error_allergen_not_found.desc()))
                    return@launch
                }
                val pollenUi = pollen.toDetailUi()
                _state.value = _state.value.copy(
                    pollen = LoadState.Loaded(pollenUi),
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
                    _events.send(UiEvent.ShowError(MR.strings.error_load_data.desc()))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(pollen = LoadState.Failed)
                _events.send(UiEvent.ShowError(MR.strings.error_load_data.desc()))
            }
        }
    }

    private fun PollenDomain.toDetailUi() = ForecastDetailPollenUi(
        name = name,
        maxLevel = maxLevel,
        severityLabels = levels.associate { it.level to it.name },
    )

    fun toggleFeelingLine() {
        _state.value = _state.value.copy(showFeelingLine = !_state.value.showFeelingLine)
    }

    private suspend fun loadTimeline(locationId: Int, pollen: PollenDomain) {
        val today = _state.value.today
        val startDate = today.minus(DatePeriod(days = TIMELINE_LOOKBACK_DAYS)).toString()
        val endDate = today.plus(DatePeriod(days = TIMELINE_LOOKAHEAD_DAYS)).toString()
        try {
            val timeline = pollenRepository.getForecastTimeline(locationId, pollenId, startDate, endDate)
                .toImmutableList()

            val todayLevel = timeline.firstOrNull { it.date == today.toString() }
            val maxLevel = pollen.maxLevel.takeIf { it > 0 } ?: DEFAULT_MAX_LEVEL
            val currentValue = todayLevel?.value ?: 0
            val scoreRatio = if (maxLevel > 0) currentValue.toDouble() / maxLevel * 10 else 0.0

            val severityLevel = computeSeverityLevel(currentValue, maxLevel)
            val pollenUi = (_state.value.pollen as? LoadState.Loaded)?.data
            val severityLabel = pollenUi?.severityLabels?.get(currentValue) ?: ""

            val healthEntries = healthRepository.observeEntries().first()
            val feelingValues = mapFeelingValues(timeline, healthEntries, maxLevel)

            val stats = computeStats(timeline, healthEntries)

            _state.value = _state.value.copy(
                timeline = LoadState.Loaded(timeline),
                currentScore = formatScore(scoreRatio),
                currentScoreMax = SCORE_DISPLAY_MAX,
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

    private fun computeSeverityLevel(value: Int, maxLevel: Int): Int =
        normalizeSeverity(value, maxLevel)

    private fun mapFeelingValues(
        timeline: List<LevelDomain>,
        healthEntries: List<HealthEntryDomain>,
        maxLevel: Int,
    ): ImmutableList<Int?> {
        val entryMap = healthEntries.associateBy { it.date }
        return timeline.map { level ->
            val entry = entryMap[level.date]
            entry?.let { feelingToChartValue(it.feeling, maxLevel) }
        }.toImmutableList()
    }

    private fun feelingToChartValue(feeling: Feeling, maxLevel: Int): Int = when (feeling) {
        Feeling.GOOD -> 0
        Feeling.MIDDLE -> maxLevel / 2
        Feeling.BAD -> maxLevel
    }

    private fun computeStats(
        timeline: List<LevelDomain>,
        healthEntries: List<HealthEntryDomain>,
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
