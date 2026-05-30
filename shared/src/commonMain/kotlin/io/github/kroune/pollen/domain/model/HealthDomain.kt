package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

enum class Feeling(val apiValue: Int) {
    GOOD(0), MIDDLE(1), BAD(2);

    companion object {
        fun fromApi(value: Int): Feeling = entries.firstOrNull { it.apiValue == value } ?: GOOD
    }
}

data class HealthEntryDomain(
    val id: Long = 0,
    val date: LocalDate,
    val feeling: Feeling,
    val eyes: Int = 0,
    val nose: Int = 0,
    val throat: Int = 0,
    val lungs: Int = 0,
    val general: Int = 0,
    val other: String = "",
    val locationName: String = "",
    val isSynced: Boolean = false,
    val tags: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationId: Int? = null,
)
