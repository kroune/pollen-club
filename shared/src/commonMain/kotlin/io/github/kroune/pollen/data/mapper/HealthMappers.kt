package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.HealthEntryEntity
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.HealthEntryDomain

fun HealthEntryEntity.toDomain(): HealthEntryDomain = HealthEntryDomain(
    id = id,
    date = date,
    feeling = Feeling.fromApi(feeling),
    eyes = eyes,
    nose = nose,
    throat = throat,
    lungs = lungs,
    general = general,
    other = other,
    locationName = locationName,
    isSynced = isSynced,
    tags = tags,
    latitude = latitude,
    longitude = longitude,
    locationId = locationId,
)

fun HealthEntryDomain.toEntity(userId: Long): HealthEntryEntity = HealthEntryEntity(
    id = id,
    userId = userId,
    date = date,
    feeling = feeling.apiValue,
    eyes = eyes,
    nose = nose,
    throat = throat,
    lungs = lungs,
    general = general,
    other = other,
    locationName = locationName,
    isSynced = isSynced,
    tags = tags,
    latitude = latitude,
    longitude = longitude,
    locationId = locationId,
)
