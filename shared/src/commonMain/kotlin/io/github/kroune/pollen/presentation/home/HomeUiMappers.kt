package io.github.kroune.pollen.presentation.home

import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

fun PersonalPollenIndexDomain.toUi(severityLabels: List<StringDesc>): HomePersonalIndexUi {
    val level = ((value / maxPossible) * 5).toInt().coerceIn(0, 5)
    val label = severityLabels.getOrElse(level) { severityLabels.first() }
    val wholepart = value.toInt()
    val fractpart = ((value - wholepart) * 10).toInt()
    val scoreFormatted = "$wholepart,$fractpart"
    return HomePersonalIndexUi(
        score = scoreFormatted,
        severityLevel = level,
        label = label,
    )
}

fun List<DayForecastSummaryDomain>.toUi(
    dayOfWeekNames: List<StringDesc>,
): ImmutableList<HomeDayForecastUi> = map { summary ->
    val date = LocalDate.parse(summary.date)
    val dowIndex = date.dayOfWeek.ordinal
    HomeDayForecastUi(
        dayOfMonth = date.dayOfMonth,
        dayOfWeek = dayOfWeekNames.getOrElse(dowIndex) { dayOfWeekNames.first() },
        severity = summary.maxSeverity,
        date = summary.date,
    )
}.toImmutableList()

fun mapAllergenItems(
    pollens: List<PollenDomain>,
    levels: List<LevelDomain>,
    sensitivities: List<AllergenSensitivityDomain>,
): Pair<ImmutableList<HomeAllergenItemUi>, ImmutableList<HomeOtherAllergenUi>> {
    val sensitivityMap = sensitivities.associateBy { it.pollenId }
    val levelMap = levels.associateBy { it.pollenId }

    val userAllergens = mutableListOf<HomeAllergenItemUi>()
    val otherAllergens = mutableListOf<HomeOtherAllergenUi>()

    for (pollen in pollens) {
        val sensitivity = sensitivityMap[pollen.id]
        if (sensitivity != null && sensitivity.level != SensitivityLevel.NONE) {
            val level = levelMap[pollen.id]
            userAllergens.add(
                HomeAllergenItemUi(
                    pollenId = pollen.id,
                    name = pollen.name,
                    severity = level?.value ?: 0,
                ),
            )
        } else {
            otherAllergens.add(
                HomeOtherAllergenUi(
                    pollenId = pollen.id,
                    name = pollen.name,
                ),
            )
        }
    }

    return userAllergens.toImmutableList() to otherAllergens.toImmutableList()
}
