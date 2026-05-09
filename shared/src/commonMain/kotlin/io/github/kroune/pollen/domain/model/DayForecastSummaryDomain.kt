package io.github.kroune.pollen.domain.model

data class DayForecastSummaryDomain(
    val date: String,
    val maxSeverity: Int,
    val dominantPollenId: Int?,
)
