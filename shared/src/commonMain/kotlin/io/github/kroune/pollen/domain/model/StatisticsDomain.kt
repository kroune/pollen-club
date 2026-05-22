package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class StatisticDomain(
    val id: Int,
    val date: LocalDate?,
    val locationId: Int,
    val good: Int,
    val middle: Int,
    val bad: Int,
)
