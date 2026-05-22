package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import io.github.kroune.pollen.util.normalizeSeverity

@Composable
fun formatScoreLocalized(value: Double): String {
    val wholePart = value.toInt()
    val fractPart = ((value - wholePart) * 10).toInt()
    return stringResource(MR.strings.score_format, wholePart, fractPart)
}

fun PersonalPollenIndexDomain.toUi(severityLabels: List<StringDesc>): HomePersonalIndexUi {
    val level = normalizeSeverity(value.toInt(), maxPossible.toInt())
    val label = severityLabels.getOrElse(level) { severityLabels.first() }
    return HomePersonalIndexUi(
        score = value,
        severityLevel = level,
        label = label,
    )
}

fun List<DayForecastSummaryDomain>.toUi(
    dayOfWeekNames: List<StringDesc>,
): ImmutableList<HomeDayForecastUi> = map { summary ->
    val date = summary.date
    val dowIndex = date.dayOfWeek.ordinal
    HomeDayForecastUi(
        dayOfMonth = date.day,
        dayOfWeek = dayOfWeekNames.getOrElse(dowIndex) { dayOfWeekNames.first() },
        severity = summary.maxSeverity,
        date = summary.date,
    )
}.toImmutableList()
