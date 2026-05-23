package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.ForecastLevelEntity
import io.github.kroune.pollen.data.local.db.entity.LevelEntity
import io.github.kroune.pollen.data.local.db.entity.StatisticsEntity
import io.github.kroune.pollen.data.remote.dto.response.LevelDto
import io.github.kroune.pollen.data.remote.dto.response.StatisticDto
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.StatisticDomain
import kotlinx.datetime.LocalDate

fun LevelDto.toLevelEntity(): LevelEntity = LevelEntity(
    id = id,
    date = LocalDate.parse(date),
    pollenId = pollen,
    locationId = location,
    value = value,
)

fun LevelDto.toForecastEntity(): ForecastLevelEntity = ForecastLevelEntity(
    id = id,
    date = LocalDate.parse(date),
    pollenId = pollen,
    locationId = location,
    value = value,
)

fun LevelEntity.toDomain(): LevelDomain = LevelDomain(
    id = id,
    date = date,
    pollenId = pollenId,
    locationId = locationId,
    value = value,
)

fun ForecastLevelEntity.toDomain(): LevelDomain = LevelDomain(
    id = id,
    date = date,
    pollenId = pollenId,
    locationId = locationId,
    value = value,
)

fun StatisticDto.toEntity(): StatisticsEntity = StatisticsEntity(
    id = id,
    date = date?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    locationId = location,
    good = good,
    middle = middle,
    bad = bad,
)

fun StatisticsEntity.toDomain(): StatisticDomain = StatisticDomain(
    id = id,
    date = date,
    locationId = locationId,
    good = good,
    middle = middle,
    bad = bad,
)
