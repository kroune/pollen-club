package io.github.kroune.pollen.domain.model

data class PersonalPollenIndexDomain(
    val value: Double,
    val maxPossible: Double,
    val dominantAllergenName: String?,
)
