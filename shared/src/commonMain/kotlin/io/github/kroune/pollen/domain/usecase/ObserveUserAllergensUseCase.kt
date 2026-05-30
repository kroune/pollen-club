package io.github.kroune.pollen.domain.usecase

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.KnownPollens
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.UserAllergen
import io.github.kroune.pollen.domain.model.UserAllergenProfile
import io.github.kroune.pollen.domain.model.activeSensitivities
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

private const val MAX_INDEX_SCORE = 10.0

/**
 * Reactive source of the user's allergen profile for a given location and day.
 *
 * It observes the repositories directly, so any change the user makes (adjusting a sensitivity,
 * a freshly synced level) flows back into the emitted [UserAllergenProfile] with no manual reload.
 */
class ObserveUserAllergensUseCase(
    private val pollenRepository: PollenRepository,
    private val sensitivityRepository: SensitivityRepository,
) {
    operator fun invoke(locationId: Int, date: LocalDate): Flow<UserAllergenProfile> =
        combine(
            pollenRepository.observePollens(),
            sensitivityRepository.observeAll(),
            observeLevelsForDay(locationId, date),
        ) { pollens, sensitivities, dayLevels ->
            buildProfile(pollens, sensitivities, dayLevels)
        }

    /** Measured levels for the day, falling back to the forecast when no measurement exists yet. */
    private fun observeLevelsForDay(locationId: Int, date: LocalDate): Flow<List<LevelDomain>> =
        combine(
            pollenRepository.observeLevelsForLocation(locationId),
            pollenRepository.observeForecastsForLocation(locationId),
        ) { measured, forecast ->
            measured.filter { it.date == date }
                .ifEmpty { forecast.filter { it.date == date } }
        }

    private fun buildProfile(
        pollens: List<PollenDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        dayLevels: List<LevelDomain>,
    ): UserAllergenProfile {
        val activeSensitivities = sensitivities.activeSensitivities()
        val sensitivePollenIds = activeSensitivities.mapTo(mutableSetOf()) { it.pollenId }
        val levelByPollenId = dayLevels.associateBy { it.pollenId }

        val allergens = pollens
            .filter { it.id in sensitivePollenIds }
            .map { pollen ->
                UserAllergen(
                    pollen = pollen,
                    level = levelByPollenId[pollen.id]?.value ?: 0,
                )
            }

        val otherPollens = pollens.filter { it.id !in sensitivePollenIds }

        val index = computeIndex(
            activeSensitivities = activeSensitivities,
            pollensById = pollens.associateBy { it.id },
            levelByPollenId = levelByPollenId,
        )

        return UserAllergenProfile(
            allergens = allergens,
            otherPollens = otherPollens,
            index = index,
        )
    }

    /**
     * Weighted blend of the user's active allergen levels, or null when there is nothing to score
     * (no sensitivities set, or no levels available for the day).
     *
     * Each level is normalised against [KnownPollens.MAX_LEVEL] — the worst level on the universal
     * scale — rather than the pollen's own ceiling, so a level number means the same severity
     * regardless of which pollen it belongs to.
     */
    private fun computeIndex(
        activeSensitivities: List<AllergenSensitivityDomain>,
        pollensById: Map<Int, PollenDomain>,
        levelByPollenId: Map<Int, LevelDomain>,
    ): PersonalPollenIndexDomain? {
        if (activeSensitivities.isEmpty() || levelByPollenId.isEmpty()) return null

        var weightedSum = 0.0
        var totalWeight = 0.0
        var strongestContribution = 0.0
        var dominantPollen: PollenDomain? = null

        for (sensitivity in activeSensitivities) {
            val pollen = pollensById[sensitivity.pollenId] ?: continue
            val weight = sensitivity.level.value.toDouble()
            totalWeight += weight

            val level = levelByPollenId[sensitivity.pollenId]
            if (level == null || level.value <= 0) continue

            val normalizedLevel = level.value.toDouble() / KnownPollens.MAX_LEVEL
            val contribution = normalizedLevel * weight
            weightedSum += contribution
            if (contribution > strongestContribution) {
                strongestContribution = contribution
                dominantPollen = pollen
            }
        }

        val score = if (totalWeight > 0) (weightedSum / totalWeight) * MAX_INDEX_SCORE else 0.0
        return PersonalPollenIndexDomain(
            score = score,
            maxScore = MAX_INDEX_SCORE,
            severityLevel = severityForScore(score),
            dominantAllergenName = dominantPollen?.name,
        )
    }

    private fun severityForScore(score: Double): Int =
        (score / MAX_INDEX_SCORE * KnownPollens.MAX_LEVEL).toInt()
            .coerceIn(0, KnownPollens.MAX_LEVEL)
}
