package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.MedicationIntakeEntity
import io.github.kroune.pollen.domain.model.MedicationIntakeDomain

fun MedicationIntakeEntity.toDomain() = MedicationIntakeDomain(
    id = id,
    therapyId = therapyId,
    date = date,
    taken = taken,
)

fun MedicationIntakeDomain.toEntity() = MedicationIntakeEntity(
    id = id,
    therapyId = therapyId,
    date = date,
    taken = taken,
)
