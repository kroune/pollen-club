package io.github.kroune.pollen.domain.model

data class MedicationIntakeDomain(
    val id: Long = 0,
    val therapyId: Long,
    val date: String,
    val taken: Boolean,
)
