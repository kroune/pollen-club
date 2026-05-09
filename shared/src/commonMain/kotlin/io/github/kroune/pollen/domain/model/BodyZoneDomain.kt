package io.github.kroune.pollen.domain.model

enum class BodyZone {
    EYES, NOSE, THROAT, CHEST, SKIN
}

data class SymptomTagDomain(
    val key: String,
    val zone: BodyZone,
    val name: String,
)
