package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.local.db.entity.FriendEntity
import io.github.kroune.pollen.data.remote.dto.response.CommentDto
import io.github.kroune.pollen.data.remote.dto.response.FriendDto
import io.github.kroune.pollen.data.remote.dto.response.FriendFeelDto
import io.github.kroune.pollen.data.remote.dto.response.MediaItemDto
import io.github.kroune.pollen.data.remote.dto.response.PinDto
import io.github.kroune.pollen.data.remote.dto.response.VkPostDto
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.CommentDomain
import io.github.kroune.pollen.domain.model.ExpertRegistry
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.FriendDomain
import io.github.kroune.pollen.domain.model.FriendFeelDomain
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MediaItemDomain
import io.github.kroune.pollen.domain.model.MediaType
import io.github.kroune.pollen.domain.model.VkPostDomain

private val VIDEO_EXTENSIONS = setOf("mp4", "avi", "mov", "webm", "mkv", "3gp")

fun CommentDto.toDomain(locale: AppLocale): CommentDomain {
    val expertId = expert.toIntOrNull() ?: 0
    return CommentDomain(
        id = id,
        date = date,
        expertId = expertId,
        expert = ExpertRegistry.get(expertId, locale).name,
        text = if (locale == AppLocale.RU) comment else commentEng,
        locationId = locationsId,
        pinned = pin == 1,
    )
}

fun VkPostDto.toDomain(): VkPostDomain = VkPostDomain(
    id = id,
    date = date,
    location = location,
    content = information,
)

fun MediaItemDto.toDomain(locale: AppLocale): MediaItemDomain = MediaItemDomain(
    id = id,
    date = date,
    type = if (mediaType == "video" || url.substringAfterLast('.').lowercase() in VIDEO_EXTENSIONS) {
        MediaType.VIDEO
    } else {
        MediaType.IMAGE
    },
    url = url,
    description = (if (locale == AppLocale.RU) infoRus else infoEng).trim(),
)

fun FriendFeelDto.toDomain(): FriendFeelDomain = FriendFeelDomain(
    id = id,
    location = location,
    date = date,
    friendId = friendId,
    feeling = Feeling.fromApi(opinion),
)

fun FriendDto.toEntity(): FriendEntity = FriendEntity(
    id = id,
    friendId = friendId,
)

fun FriendEntity.toDomain(): FriendDomain = FriendDomain(
    id = id,
    friendId = friendId,
    name = name,
)

fun PinDto.toDomain(): MapPinDomain = MapPinDomain(
    date = date,
    feeling = Feeling.fromApi(value),
    latitude = latitude,
    longitude = longitude,
    pollenType = pollenType,
    tags = tags,
    friendId = friendId,
)
