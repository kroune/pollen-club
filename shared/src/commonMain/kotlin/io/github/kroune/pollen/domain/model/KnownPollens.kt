package io.github.kroune.pollen.domain.model

object KnownPollens {
    // Phenology observations track birch budding/flowering.
    const val BIRCH_POLLEN_ID: Int = 1

    // Fallback maxLevel for a pollen when the server hasn't provided one.
    // Used by severity normalization to avoid divide-by-zero or extreme buckets.
    const val DEFAULT_MAX_LEVEL: Int = 10
}
