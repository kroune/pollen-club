package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import io.github.kroune.pollen.domain.model.PollenDomain

interface PersonalIndexRepository {
    suspend fun computePersonalIndex(
        levels: List<LevelDomain>,
        sensitivities: List<AllergenSensitivityDomain>,
        pollens: List<PollenDomain>,
    ): PersonalPollenIndexDomain

    suspend fun computeDayForecastSummaries(
        locationId: Int,
        sensitivities: List<AllergenSensitivityDomain>,
        days: Int = 7,
    ): List<DayForecastSummaryDomain>
}
