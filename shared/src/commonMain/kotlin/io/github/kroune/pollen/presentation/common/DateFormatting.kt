package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import kotlinx.datetime.LocalDate

private val MONTH_GENITIVE_RESOURCES: Array<StringResource> = arrayOf(
    MR.strings.month_january_gen,
    MR.strings.month_february_gen,
    MR.strings.month_march_gen,
    MR.strings.month_april_gen,
    MR.strings.month_may_gen,
    MR.strings.month_june_gen,
    MR.strings.month_july_gen,
    MR.strings.month_august_gen,
    MR.strings.month_september_gen,
    MR.strings.month_october_gen,
    MR.strings.month_november_gen,
    MR.strings.month_december_gen,
)

@Composable
fun formatDateLocalized(date: LocalDate): String {
    val monthName = stringResource(MONTH_GENITIVE_RESOURCES[date.month.ordinal])
    return stringResource(MR.strings.date_day_month, date.day, monthName)
}
