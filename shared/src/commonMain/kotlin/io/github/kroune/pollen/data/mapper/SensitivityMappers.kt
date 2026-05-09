package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.AllergenSensitivityEntity
import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel

fun AllergenSensitivityEntity.toDomain() = AllergenSensitivityDomain(
    pollenId = pollenId,
    level = SensitivityLevel.fromValue(sensitivityLevel),
)

fun AllergenSensitivityDomain.toEntity() = AllergenSensitivityEntity(
    pollenId = pollenId,
    sensitivityLevel = level.value,
)
