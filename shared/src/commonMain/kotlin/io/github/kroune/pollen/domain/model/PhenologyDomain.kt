package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class PhenologyObservationDomain(
    val id: Long = 0,
    val date: LocalDate,
    val time: Long,
    val state: Int,
    val latitude: Double,
    val longitude: Double,
    val comment: String,
)

data class HashTagDomain(
    val id: String,
    val value: String,
    val name: String?,
)
