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
