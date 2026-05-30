package io.github.kroune.pollen.domain.model

/**
 * The user's aggregate pollen burden for a single day.
 *
 * [score] is a weighted blend of the user's active allergen levels (each normalised against the
 * universal 0..5 level scale, then weighted by sensitivity), rescaled to 0..[maxScore].
 */
data class PersonalPollenIndexDomain(
    val score: Double,
    val maxScore: Double,
    val severityLevel: Int,
    val dominantAllergenName: String?,
)
