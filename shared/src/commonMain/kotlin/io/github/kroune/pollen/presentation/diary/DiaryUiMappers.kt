package io.github.kroune.pollen.presentation.diary

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
        dayOfWeek = russianDayOfWeek(date.dayOfWeek),
        isSelected = date == selectedDate,
        isoDate = date.toString(),
    )
}.toImmutableList()

fun buildMoodOptions(selectedFeeling: Feeling?): ImmutableList<DiaryMoodOptionUi> = listOf(
    DiaryMoodOptionUi(Feeling.GOOD, "Хорошо", selectedFeeling == Feeling.GOOD),
    DiaryMoodOptionUi(Feeling.MIDDLE, "Терпимо", selectedFeeling == Feeling.MIDDLE),
    DiaryMoodOptionUi(Feeling.BAD, "Плохо", selectedFeeling == Feeling.BAD),
).toImmutableList()

fun mapBodyZones(
    selectedTagKeys: Set<String>,
    selectedZone: BodyZone?,
): ImmutableList<DiaryBodyZoneUi> = BodyZone.entries.map { zone ->
    DiaryBodyZoneUi(
        zone = zone,
        label = russianZoneLabel(zone),
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

fun russianMonthLabel(date: LocalDate): String {
    val month = when (date.month) {
        Month.JANUARY -> "Январь"
        Month.FEBRUARY -> "Февраль"
        Month.MARCH -> "Март"
        Month.APRIL -> "Апрель"
        Month.MAY -> "Май"
        Month.JUNE -> "Июнь"
        Month.JULY -> "Июль"
        Month.AUGUST -> "Август"
        Month.SEPTEMBER -> "Сентябрь"
        Month.OCTOBER -> "Октябрь"
        Month.NOVEMBER -> "Ноябрь"
        Month.DECEMBER -> "Декабрь"
    }
    return "$month ${date.year}"
}

fun russianDayOfWeek(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "пн"
    DayOfWeek.TUESDAY -> "вт"
    DayOfWeek.WEDNESDAY -> "ср"
    DayOfWeek.THURSDAY -> "чт"
    DayOfWeek.FRIDAY -> "пт"
    DayOfWeek.SATURDAY -> "сб"
    DayOfWeek.SUNDAY -> "вс"
}

fun russianZoneLabel(zone: BodyZone): String = when (zone) {
    BodyZone.EYES -> "Глаза"
    BodyZone.NOSE -> "Нос"
    BodyZone.THROAT -> "Горло"
    BodyZone.CHEST -> "Грудь"
    BodyZone.SKIN -> "Кожа"
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
