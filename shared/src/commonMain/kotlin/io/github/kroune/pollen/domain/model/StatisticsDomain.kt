package io.github.kroune.pollen.domain.model

data class StatisticDomain(
    val id: Int,
    val date: String?,
    val locationId: Int,
    val good: Int,
    val middle: Int,
    val bad: Int,
)
