package io.github.kroune.pollen.presentation.diary

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.BodyZone
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.MedicationIntakeDomain
import io.github.kroune.pollen.domain.model.SymptomTagRegistry
import io.github.kroune.pollen.domain.model.TherapyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun buildDates(
    weekStart: LocalDate,
    selectedDate: LocalDate,
): ImmutableList<DiaryDateUi> = (0..6).map { offset ->
    val date = weekStart.plus(offset, DateTimeUnit.DAY)
    DiaryDateUi(
        dayOfMonth = date.day,
        dayOfWeek = dayOfWeekStringDesc(date.dayOfWeek),
        isSelected = date == selectedDate,
        isoDate = date.toString(),
    )
}.toImmutableList()

fun buildMoodOptions(selectedFeeling: Feeling?): ImmutableList<DiaryMoodOptionUi> = listOf(
    DiaryMoodOptionUi(Feeling.GOOD, MR.strings.feeling_good.desc(), selectedFeeling == Feeling.GOOD),
    DiaryMoodOptionUi(Feeling.MIDDLE, MR.strings.feeling_moderate.desc(), selectedFeeling == Feeling.MIDDLE),
    DiaryMoodOptionUi(Feeling.BAD, MR.strings.feeling_bad.desc(), selectedFeeling == Feeling.BAD),
).toImmutableList()

fun mapBodyZones(
    selectedTagKeys: Set<String>,
    selectedZone: BodyZone?,
): ImmutableList<DiaryBodyZoneUi> = BodyZone.entries.map { zone ->
    DiaryBodyZoneUi(
        zone = zone,
        label = zoneStringDesc(zone),
        symptomCount = selectedTagKeys.count { it.startsWith(zonePrefix(zone)) },
        isSelected = zone == selectedZone,
    )
}.toImmutableList()

fun mapZoneTags(
    zone: BodyZone?,
    selectedTagKeys: Set<String>,
    locale: AppLocale = AppLocale.RU,
): ImmutableList<DiarySymptomTagUi> {
    if (zone == null) return emptyList<DiarySymptomTagUi>().toImmutableList()
    return SymptomTagRegistry.getTagsByZone(zone, locale).map { tag ->
        DiarySymptomTagUi(
            key = tag.key,
            label = tag.name,
            isSelected = tag.key in selectedTagKeys,
        )
    }.toImmutableList()
}

fun mapTherapyItems(
    therapies: List<TherapyDomain>,
    intakes: List<MedicationIntakeDomain>,
): ImmutableList<DiaryTherapyItemUi> {
    val intakeMap = intakes.associateBy { it.therapyId }
    return therapies.map { therapy ->
        val intake = intakeMap[therapy.id]
        DiaryTherapyItemUi(
            therapyId = therapy.id,
            name = therapy.cureName,
            dosage = therapy.dose,
            time = "",
            taken = intake?.taken ?: false,
        )
    }.toImmutableList()
}

fun monthStringDesc(month: Month): StringDesc = when (month) {
    Month.JANUARY -> MR.strings.month_january.desc()
    Month.FEBRUARY -> MR.strings.month_february.desc()
    Month.MARCH -> MR.strings.month_march.desc()
    Month.APRIL -> MR.strings.month_april.desc()
    Month.MAY -> MR.strings.month_may.desc()
    Month.JUNE -> MR.strings.month_june.desc()
    Month.JULY -> MR.strings.month_july.desc()
    Month.AUGUST -> MR.strings.month_august.desc()
    Month.SEPTEMBER -> MR.strings.month_september.desc()
    Month.OCTOBER -> MR.strings.month_october.desc()
    Month.NOVEMBER -> MR.strings.month_november.desc()
    Month.DECEMBER -> MR.strings.month_december.desc()
}

fun monthShortStringDesc(month: Month): StringDesc = when (month) {
    Month.JANUARY -> MR.strings.month_jan_short.desc()
    Month.FEBRUARY -> MR.strings.month_feb_short.desc()
    Month.MARCH -> MR.strings.month_mar_short.desc()
    Month.APRIL -> MR.strings.month_apr_short.desc()
    Month.MAY -> MR.strings.month_may_short.desc()
    Month.JUNE -> MR.strings.month_jun_short.desc()
    Month.JULY -> MR.strings.month_jul_short.desc()
    Month.AUGUST -> MR.strings.month_aug_short.desc()
    Month.SEPTEMBER -> MR.strings.month_sep_short.desc()
    Month.OCTOBER -> MR.strings.month_oct_short.desc()
    Month.NOVEMBER -> MR.strings.month_nov_short.desc()
    Month.DECEMBER -> MR.strings.month_dec_short.desc()
}

fun dayOfWeekStringDesc(dayOfWeek: DayOfWeek): StringDesc = when (dayOfWeek) {
    DayOfWeek.MONDAY -> MR.strings.dow_mon.desc()
    DayOfWeek.TUESDAY -> MR.strings.dow_tue.desc()
    DayOfWeek.WEDNESDAY -> MR.strings.dow_wed.desc()
    DayOfWeek.THURSDAY -> MR.strings.dow_thu.desc()
    DayOfWeek.FRIDAY -> MR.strings.dow_fri.desc()
    DayOfWeek.SATURDAY -> MR.strings.dow_sat.desc()
    DayOfWeek.SUNDAY -> MR.strings.dow_sun.desc()
}

fun zoneStringDesc(zone: BodyZone): StringDesc = when (zone) {
    BodyZone.EYES -> MR.strings.zone_eyes.desc()
    BodyZone.NOSE -> MR.strings.zone_nose.desc()
    BodyZone.THROAT -> MR.strings.zone_throat.desc()
    BodyZone.CHEST -> MR.strings.zone_chest.desc()
    BodyZone.SKIN -> MR.strings.zone_skin.desc()
}


fun zonePrefix(zone: BodyZone): String = when (zone) {
    BodyZone.EYES -> "eyes_"
    BodyZone.NOSE -> "nose_"
    BodyZone.THROAT -> "throat_"
    BodyZone.CHEST -> "chest_"
    BodyZone.SKIN -> "skin_"
}

fun LocalDate.startOfWeek(): LocalDate {
    val offset = when (dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }
    return minus(offset, DateTimeUnit.DAY)
}
