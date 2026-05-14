package io.github.kroune.pollen.presentation.home

import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import io.github.kroune.pollen.util.normalizeSeverity
import kotlinx.datetime.LocalDate

fun formatScore(value: Double): String {
    val wholePart = value.toInt()
    val fractPart = ((value - wholePart) * 10).toInt()
    return "$wholePart,$fractPart"
}

fun PersonalPollenIndexDomain.toUi(severityLabels: List<StringDesc>): HomePersonalIndexUi {
    val level = normalizeSeverity(value.toInt(), maxPossible.toInt())
    val label = severityLabels.getOrElse(level) { severityLabels.first() }
    return HomePersonalIndexUi(
        score = formatScore(value),
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
        dayOfMonth = date.day,
        dayOfWeek = dayOfWeekNames.getOrElse(dowIndex) { dayOfWeekNames.first() },
        severity = summary.maxSeverity,
        date = summary.date,
    )
}.toImmutableList()
