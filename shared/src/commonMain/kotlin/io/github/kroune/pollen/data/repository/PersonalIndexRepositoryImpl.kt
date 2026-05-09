package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class PersonalIndexRepositoryImpl(
    private val pollenRepository: PollenRepository,
) : PersonalIndexRepository {

    override suspend fun computePersonalIndex(
        levels: List<LevelDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        pollens: List<PollenDomain>,
    ): PersonalPollenIndexDomain {
        val levelMap = levels.associateBy { it.pollenId }
        var weightedSum = 0.0
        var maxPossible = 0.0
        var maxContribution = 0.0
        var dominantPollen: PollenDomain? = null

        val activeSensitivities = sensitivities.filter { it.level != SensitivityLevel.NONE }
        for (sensitivity in activeSensitivities) {
            val pollen = pollens.firstOrNull { it.id == sensitivity.pollenId } ?: continue
            val weight = sensitivity.level.value.toDouble()
            maxPossible += weight
            val level = levelMap[sensitivity.pollenId]
            if (level == null || level.value == 0) continue
            val normalizedLevel =
                if (pollen.maxLevel > 0) level.value.toDouble() / pollen.maxLevel else 0.0
            val contribution = normalizedLevel * weight
            weightedSum += contribution
            if (contribution > maxContribution) {
                maxContribution = contribution
                dominantPollen = pollen
            }
        }
        val score = if (maxPossible > 0) (weightedSum / maxPossible) * 10.0 else 0.0
        return PersonalPollenIndexDomain(
            value = score,
            maxPossible = 10.0,
            dominantAllergenName = dominantPollen?.name,
        )
    }

    override suspend fun computeDayForecastSummaries(
        locationId: Int,
        sensitivities: List<AllergenSensitivityDomain>,
        days: Int,
    ): List<DayForecastSummaryDomain> {
        val sensitivePollenIds = sensitivities
            .filter { it.level != SensitivityLevel.NONE }
            .map { it.pollenId }
            .toSet()
        if (sensitivePollenIds.isEmpty()) return emptyList()

        val today = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val endDate = today.plus(days - 1, DateTimeUnit.DAY)

        val allForecasts = sensitivePollenIds.flatMap { pollenId ->
            pollenRepository.getForecastTimeline(
                locationId, pollenId, today.toString(), endDate.toString(),
            )
        }

        return (0 until days).map { offset ->
            val date = today.plus(offset, DateTimeUnit.DAY).toString()
            val dayLevels = allForecasts.filter { it.date == date }
            val maxLevel = dayLevels.maxOfOrNull { it.value } ?: 0
            val dominantId = dayLevels.maxByOrNull { it.value }?.pollenId
            DayForecastSummaryDomain(
                date = date,
                maxSeverity = maxLevel,
                dominantPollenId = dominantId,
            )
        }
    }
}
