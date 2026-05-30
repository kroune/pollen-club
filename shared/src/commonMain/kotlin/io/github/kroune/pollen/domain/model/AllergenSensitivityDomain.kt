package io.github.kroune.pollen.domain.model

enum class SensitivityLevel(val value: Int) {
    NONE(0), LIGHT(1), MODERATE(2), SEVERE(3);

    companion object {
        fun fromValue(v: Int): SensitivityLevel =
            entries.firstOrNull { it.value == v } ?: NONE
    }
}

data class AllergenSensitivityDomain(
    val pollenId: Int,
    val level: SensitivityLevel,
)

/** Sensitivities the user actually reacts to (anything above [SensitivityLevel.NONE]). */
fun List<AllergenSensitivityDomain>.activeSensitivities(): List<AllergenSensitivityDomain> =
    filter { it.level != SensitivityLevel.NONE }

/** Pollen ids the user is sensitive to. */
fun List<AllergenSensitivityDomain>.sensitivePollenIds(): Set<Int> =
    activeSensitivities().mapTo(mutableSetOf()) { it.pollenId }

/** Pollen id of the user's most-sensitive allergen, or null when none is set. */
fun List<AllergenSensitivityDomain>.primaryPollenId(): Int? =
    activeSensitivities().maxByOrNull { it.level.value }?.pollenId
