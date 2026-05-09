package io.github.kroune.pollen.util

import io.github.kroune.pollen.domain.model.AppLocale

private val MONTHS_RU = arrayOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

private val MONTHS_EN = arrayOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

fun formatDateLocalized(date: String, locale: AppLocale): String {
    val parts = date.split("-")
    if (parts.size != 3) return date
    val month = parts[1].toIntOrNull() ?: return date
    val day = parts[2].toIntOrNull() ?: return date
    if (month !in 1..12) return date
    return when (locale) {
        AppLocale.RU -> "$day ${MONTHS_RU[month - 1]}"
        AppLocale.EN -> "${MONTHS_EN[month - 1]} $day"
    }
}
