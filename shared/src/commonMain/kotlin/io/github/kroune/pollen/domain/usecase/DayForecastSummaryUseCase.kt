package io.github.kroune.pollen.domain.usecase

import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.KnownPollens
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.repository.PollenRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Builds the home screen's week strip: for each day, the most severe pollen across the whole
 * catalogue and the level it reaches.
 *
 * A forecast value is already a severity bucket on the universal 0..5 scale, so days are compared
 * directly by value — no per-pollen normalisation.
 */
class DayForecastSummaryUseCase(
    private val pollenRepository: PollenRepository,
) {
    suspend operator fun invoke(
        locationId: Int,
        startDate: LocalDate,
        days: Int,
    ): List<DayForecastSummaryDomain> {
        val pollens = pollenRepository.observePollens().first()
        if (pollens.isEmpty()) return emptyList()

        val endDate = startDate.plus(days - 1, DateTimeUnit.DAY)
        val forecasts = pollens.flatMap { pollen ->
            pollenRepository.getForecastTimeline(locationId, pollen.id, startDate, endDate)
        }
        return buildDaySummaries(forecasts, startDate, days)
    }

    private fun buildDaySummaries(
        forecasts: List<LevelDomain>,
        startDate: LocalDate,
        days: Int,
    ): List<DayForecastSummaryDomain> = (0 until days).map { dayOffset ->
        val date = startDate.plus(dayOffset, DateTimeUnit.DAY)
        val dominantLevel = forecasts.filter { it.date == date }.maxByOrNull { it.value }
        DayForecastSummaryDomain(
            date = date,
            maxSeverity = (dominantLevel?.value ?: 0).coerceIn(0, KnownPollens.MAX_LEVEL),
            dominantPollenId = dominantLevel?.pollenId,
        )
    }
}
