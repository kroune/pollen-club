package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.PhenologyEntity
import io.github.kroune.pollen.data.local.db.entity.TherapyEntity
import io.github.kroune.pollen.data.remote.dto.response.CureActionTypeDto
import io.github.kroune.pollen.data.remote.dto.response.CureDoseDto
import io.github.kroune.pollen.data.remote.dto.response.CureDto
import io.github.kroune.pollen.data.remote.dto.response.CureFormDto
import io.github.kroune.pollen.data.remote.dto.response.CureFrequencyDto
import io.github.kroune.pollen.data.remote.dto.response.CureItemDto
import io.github.kroune.pollen.data.remote.dto.response.HashTagDto
import io.github.kroune.pollen.data.remote.dto.response.PolygonDataResponse
import io.github.kroune.pollen.data.remote.dto.response.UserForecastEntryDto
import io.github.kroune.pollen.data.remote.dto.response.UserForecastInfoDto
import io.github.kroune.pollen.data.remote.weather.CurrentWeatherDto
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.CureDomain
import io.github.kroune.pollen.domain.model.CureActionTypeDomain
import io.github.kroune.pollen.domain.model.CureDoseDomain
import io.github.kroune.pollen.domain.model.CureFormDomain
import io.github.kroune.pollen.domain.model.CureFrequencyDomain
import io.github.kroune.pollen.domain.model.CureItemDomain
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.GeoPoint
import io.github.kroune.pollen.domain.model.MapPolygonDomain
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import io.github.kroune.pollen.domain.model.TherapyDomain
import io.github.kroune.pollen.domain.model.UserForecastEntryDomain
import io.github.kroune.pollen.domain.model.UserForecastInfoDomain
import io.github.kroune.pollen.domain.model.WeatherDomain

fun CureActionTypeDto.toDomain(locale: AppLocale): CureActionTypeDomain = CureActionTypeDomain(
    id = id,
    name = if (locale == AppLocale.RU) (nameRus ?: "") else (nameEng ?: ""),
    sortNumber = sortNumber,
)

fun CureDto.toDomain(locale: AppLocale): CureDomain = CureDomain(
    id = id,
    name = if (locale == AppLocale.RU) (nameRus ?: "") else (nameEng ?: ""),
    description = if (locale == AppLocale.RU) (descRus ?: "") else (descEng ?: ""),
    forma = forma,
    sortNumber = sortNumber,
    info = if (locale == AppLocale.RU) (infoRus ?: "") else (infoEng ?: ""),
    actionType = actionType,
    items = items.map { it.toDomain(locale) },
)

fun CureItemDto.toDomain(locale: AppLocale): CureItemDomain = CureItemDomain(
    id = id,
    name = if (locale == AppLocale.RU) (nameRus ?: "") else (nameEng ?: ""),
    description = if (locale == AppLocale.RU) (descRus ?: "") else (descEng ?: ""),
    forma = forma,
    sortNumber = sortNumber,
    mark = mark,
    activeSubstance = activeSubstance,
)

fun CureFormDto.toDomain(locale: AppLocale): CureFormDomain = CureFormDomain(
    id = id,
    name = if (locale == AppLocale.RU) (nameRus ?: "") else (nameEng ?: ""),
)

fun CureDoseDto.toDomain(locale: AppLocale): CureDoseDomain = CureDoseDomain(
    id = id,
    name = if (locale == AppLocale.RU) (nameRus ?: "") else (nameEng ?: ""),
)

fun CureFrequencyDto.toDomain(locale: AppLocale): CureFrequencyDomain = CureFrequencyDomain(
    id = id,
    name = if (locale == AppLocale.RU) (nameRus ?: "") else (nameEng ?: ""),
)

fun TherapyEntity.toDomain(): TherapyDomain = TherapyDomain(
    id = id,
    date = date,
    cureTypeId = cureTypeId,
    cureName = cureName,
    cureId = cureId,
    form = form,
    dose = dose,
    frequency = frequency,
    startDate = startDate,
)

fun PhenologyEntity.toDomain(): PhenologyObservationDomain = PhenologyObservationDomain(
    id = id,
    date = date,
    time = time,
    state = state,
    latitude = latitude,
    longitude = longitude,
    comment = comment,
)

fun PhenologyObservationDomain.toEntity(): PhenologyEntity = PhenologyEntity(
    id = id,
    date = date,
    time = time,
    state = state,
    latitude = latitude,
    longitude = longitude,
    comment = comment,
)

fun HashTagDto.toDomain(): HashTagDomain = HashTagDomain(id = id, value = value, name = name)

fun PolygonDataResponse.toDomain(): MapPolygonDomain {
    val parsed = latlngs.map { polygon ->
        polygon.map { ring ->
            ring.mapNotNull { point ->
                if (point.size >= 2) GeoPoint(point[0], point[1]) else null
            }
        }
    }
    return MapPolygonDomain(
        polygons = parsed,
        color = color,
        opacity = opacity,
        fillColor = fillColor,
        fillOpacity = fillOpacity,
    )
}

fun UserForecastInfoDto.toDomain(locale: AppLocale): UserForecastInfoDomain = UserForecastInfoDomain(
    id = id ?: "",
    description = if (locale == AppLocale.RU) (descRus ?: "") else (descEng ?: ""),
)

fun UserForecastEntryDto.toDomain(): UserForecastEntryDomain = UserForecastEntryDomain(
    id = id ?: "",
    date = date ?: "",
    value = value ?: "",
)

fun CurrentWeatherDto.toDomain(): WeatherDomain = WeatherDomain(
    temperature = temperature,
    weatherCode = weatherCode,
    isDay = isDay == 1,
)
