package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.LocationEntity
import io.github.kroune.pollen.data.local.db.entity.PollenEntity
import io.github.kroune.pollen.data.local.db.entity.PollenLevelInfoEntity
import io.github.kroune.pollen.data.remote.dto.response.LocationDto
import io.github.kroune.pollen.data.remote.dto.response.PollenDto
import io.github.kroune.pollen.data.remote.dto.response.PollenLevelDto
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.PollenLevelDomain

fun PollenDto.toEntity(): PollenEntity = PollenEntity(
    id = id,
    descRu = desc,
    descEng = descEng,
    infoRu = info,
    infoEng = infoEng,
    maxLevel = maxLevel,
)

fun PollenLevelDto.toEntity(pollenId: Int): PollenLevelInfoEntity = PollenLevelInfoEntity(
    pollenId = pollenId,
    level = level,
    nameRu = name,
    nameEng = nameEng,
    infoRu = info,
    infoEng = infoEng,
    color = color,
)

fun PollenEntity.toDomain(
    locale: AppLocale,
    levelInfos: List<PollenLevelInfoEntity>,
): PollenDomain = PollenDomain(
    id = id,
    name = if (locale == AppLocale.RU) descRu else descEng,
    description = if (locale == AppLocale.RU) infoRu else infoEng,
    maxLevel = maxLevel,
    levels = levelInfos.map { it.toDomain(locale) },
)

fun PollenLevelInfoEntity.toDomain(locale: AppLocale): PollenLevelDomain = PollenLevelDomain(
    level = level,
    name = if (locale == AppLocale.RU) nameRu else nameEng,
    info = if (locale == AppLocale.RU) infoRu else infoEng,
    color = color or 0xFF000000.toInt(),
)

fun LocationDto.toEntity(): LocationEntity = LocationEntity(
    id = id,
    nameRu = desc,
    commentRu = comment,
    nameEng = engName,
    commentEng = engDesc,
    latitude = latitude,
    longitude = longitude,
)

fun LocationEntity.toDomain(locale: AppLocale): LocationDomain = LocationDomain(
    id = id,
    name = if (locale == AppLocale.RU) nameRu else nameEng,
    description = if (locale == AppLocale.RU) commentRu else commentEng,
    latitude = latitude,
    longitude = longitude,
)
