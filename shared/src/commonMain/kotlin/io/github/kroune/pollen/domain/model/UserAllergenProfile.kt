package io.github.kroune.pollen.domain.model

/**
 * Everything the home screen needs to describe the user's allergens for a given day.
 *
 * Produced reactively by ObserveUserAllergensUseCase, so it always reflects the current
 * sensitivities, pollen catalogue and levels.
 */
data class UserAllergenProfile(
    val allergens: List<UserAllergen>,
    val otherPollens: List<PollenDomain>,
    val index: PersonalPollenIndexDomain?,
)

/**
 * One pollen the user is sensitive to, paired with its level on the selected day.
 *
 * [level] is the raw server value, which is already a severity bucket on the universal 0..5 scale
 * (0 = no pollen).
 */
data class UserAllergen(
    val pollen: PollenDomain,
    val level: Int,
)
