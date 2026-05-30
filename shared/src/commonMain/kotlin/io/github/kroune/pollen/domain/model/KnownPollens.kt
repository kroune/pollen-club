package io.github.kroune.pollen.domain.model

object KnownPollens {
    // Phenology observations track birch budding/flowering.
    const val BIRCH_POLLEN_ID: Int = 1

    /**
     * Worst level on the universal severity scale. Server level values run 0..[MAX_LEVEL]
     * (0 = no pollen) and the app's severity palette has one colour per step. A given pollen may
     * cap lower (its own max_level), but the scale itself is shared, so a level number means the
     * same severity for every pollen.
     */
    const val MAX_LEVEL: Int = 5
}
