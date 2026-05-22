package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class DayForecastSummaryDomain(
    val date: LocalDate,
    val maxSeverity: Int,
    val dominantPollenId: Int?,
)
