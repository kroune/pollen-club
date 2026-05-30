package io.github.kroune.pollen.presentation.detail.viewmodel

import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain
import io.github.kroune.pollen.domain.model.KnownPollens
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.PollenIconRegistry
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.detail.DetailStatsUi
import io.github.kroune.pollen.presentation.detail.ForecastDetailPollenUi
import io.github.kroune.pollen.presentation.detail.ForecastDetailUiState
import io.github.kroune.pollen.presentation.detail.SCORE_DISPLAY_MAX
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private const val TIMELINE_LOOKBACK_DAYS = 30
private const val TIMELINE_LOOKAHEAD_DAYS = 5

class ForecastDetailViewModel(
    private val pollenId: Int,
    private val pollenRepository: PollenRepository,
    private val userSession: UserSession,
    private val locationRepository: LocationRepository,
    private val healthRepository: HealthRepository,
    private val todayProvider: TodayProvider,
) : MviViewModel<ForecastDetailUiState, ForecastDetailIntent, UiEvent>(
    ForecastDetailUiState(today = todayProvider.today.value),
) {

    init {
        observeToday()
        loadData()
    }

    override fun handleIntent(intent: ForecastDetailIntent) {
        when (intent) {
            ForecastDetailIntent.ReloadData -> loadData()
            ForecastDetailIntent.ToggleFeelingLine -> updateState { copy(showFeelingLine = !showFeelingLine) }
        }
    }

    private fun observeToday() {
        viewModelScope.launch {
            todayProvider.today.collect { today ->
                updateState { copy(today = today) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            runCatchingCancellable {
                val locationIdAsync = async {
                    userSession.currentUser().location
                        ?: locationRepository.getAll().first().id
                }
                val pollenAsync = async {
                    val pollens = pollenRepository.observePollens().first()
                    val pollen = pollens.first { it.id == pollenId }
                    updateState {
                        copy(
                            pollen = LoadState.Loaded(pollen.toDetailUi()),
                            aboutText = pollen.description,
                            pollenIconRes = PollenIconRegistry.iconFor(pollen.id),
                        )
                    }
                    pollen
                }
                val locationId = locationIdAsync.await()
                val pollen = pollenAsync.await()
                loadForecast(locationId, pollen)
            }.onFailure {
                updateState { copy(pollen = LoadState.Failed) }
                emitEffect(UiEvent.ShowError(MR.strings.error_load_data.desc()))
            }
        }
    }

    private fun PollenDomain.toDetailUi() = ForecastDetailPollenUi(
        name = name,
        maxLevel = maxLevel,
        severityLabels = levels.associate { it.level to it.name },
    )

    private suspend fun loadForecast(locationId: Int, pollen: PollenDomain) {
        val today = currentState.today ?: todayProvider.today.value
        val startDate = today.minus(DatePeriod(days = TIMELINE_LOOKBACK_DAYS))
        val endDate = today.plus(DatePeriod(days = TIMELINE_LOOKAHEAD_DAYS))
        runCatchingCancellable {
            val timeline = pollenRepository
                .getForecastTimeline(locationId, pollenId, startDate, endDate)
                .toImmutableList()

            val todayLevel = timeline.firstOrNull { it.date == today }
            // maxLevel is the pollen's own ceiling, used only to scale the chart's Y axis.
            val maxLevel = pollen.maxLevel.takeIf { it > 0 } ?: KnownPollens.MAX_LEVEL
            val currentValue = todayLevel?.value ?: 0

            // The level value is already a severity bucket on the universal 0..5 scale.
            val severityLevel = currentValue.coerceIn(0, KnownPollens.MAX_LEVEL)
            // Score is the level as a fraction of the universal max (not the pollen's own ceiling),
            // shown out of 10 — so it agrees with the severity dots above.
            val scoreRatio = currentValue.toDouble() / KnownPollens.MAX_LEVEL * 10
            val pollenUi = currentState.pollen.dataOrNull
            val severityLabel = pollenUi?.severityLabels?.get(currentValue) ?: ""

            val healthEntries = healthRepository.observeEntries().first()
            val feelingValues = mapFeelingValues(timeline, healthEntries, maxLevel)

            val stats = computeStats(timeline, healthEntries)

            updateState {
                copy(
                    timeline = LoadState.Loaded(timeline),
                    currentScore = scoreRatio,
                    currentScoreMax = SCORE_DISPLAY_MAX,
                    severityLevel = severityLevel,
                    severityLabel = severityLabel,
                    stats = stats,
                    feelingValues = feelingValues,
                )
            }
        }.onFailure {
            updateState { copy(timeline = LoadState.Failed) }
            emitEffect(UiEvent.ShowError(MR.strings.error_load_forecast.desc()))
        }
    }

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
        val peak = timeline.maxByOrNull { it.value } ?: return null

        val peakIndex = timeline.indexOf(peak)
        val decline = timeline.drop(peakIndex + 1).firstOrNull { it.value < peak.value }

        val timelineDates = timeline.map { it.date }.toSet()
        val symptomCount =
            healthEntries.count { it.date in timelineDates && it.feeling == Feeling.BAD }

        return DetailStatsUi(
            peakDate = peak.date,
            declineDate = decline?.date,
            symptomCount = symptomCount,
        )
    }
}
