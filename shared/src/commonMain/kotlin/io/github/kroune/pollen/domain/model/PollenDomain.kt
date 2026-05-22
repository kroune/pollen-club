package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class PollenDomain(
    val id: Int,
    val name: String,
    val description: String,
    val maxLevel: Int,
    val levels: List<PollenLevelDomain>,
)

data class PollenLevelDomain(
    val level: Int,
    val name: String,
    val info: String,
    val color: Int,
)

data class LevelDomain(
    val id: Int,
    val date: LocalDate,
    val pollenId: Int,
    val locationId: Int,
    val value: Int,
)
