package io.github.kroune.pollen.presentation.home

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.PersonalPollenIndexDomain
import io.github.kroune.pollen.domain.model.UserAllergen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** Labels for the universal 0..5 level scale, indexed by level. */
private val severityLabels: List<StringDesc> = listOf(
    MR.strings.severity_none.desc(),
    MR.strings.severity_low.desc(),
    MR.strings.severity_medium.desc(),
    MR.strings.severity_high.desc(),
    MR.strings.severity_very_high.desc(),
    MR.strings.severity_extra.desc(),
)

@Composable
fun formatScoreLocalized(value: Double): String {
    val wholePart = value.toInt()
    val fractPart = ((value - wholePart) * 10).toInt()
    return stringResource(MR.strings.score_format, wholePart, fractPart)
}

fun UserAllergen.toRowData(): AllergenRowData =
    AllergenRowData(pollen = pollen, level = level)

fun PersonalPollenIndexDomain.toUi(): HomePersonalIndexUi =
    HomePersonalIndexUi(
        score = score,
        severityLevel = severityLevel,
        label = severityLabels.getOrElse(severityLevel) { severityLabels.first() },
    )

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
