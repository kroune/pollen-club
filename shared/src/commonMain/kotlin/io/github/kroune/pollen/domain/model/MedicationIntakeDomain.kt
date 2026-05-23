package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class MedicationIntakeDomain(
    val id: Long = 0,
    val therapyId: Long,
    val date: LocalDate,
    val taken: Boolean,
)
