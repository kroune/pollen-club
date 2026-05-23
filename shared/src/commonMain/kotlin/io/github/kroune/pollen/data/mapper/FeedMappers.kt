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
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.FriendDomain
import io.github.kroune.pollen.domain.model.FriendFeelDomain
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MediaItemDomain
import io.github.kroune.pollen.domain.model.MediaType
import io.github.kroune.pollen.domain.model.VkPostDomain
import io.github.kroune.pollen.presentation.common.ExpertRegistry
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

private val VIDEO_EXTENSIONS = setOf("mp4", "avi", "mov", "webm", "mkv", "3gp")

// Server sends VK posts as "UserName: content text". Names are 1-3 words, letters only.
private const val MAX_NAME_PREFIX_LENGTH = 40
private val NAME_PATTERN = Regex("^[\\p{L} .'-]+$")

fun CommentDto.toDomain(locale: AppLocale): CommentDomain {
    val expertId = expert.toIntOrNull() ?: 0
    return CommentDomain(
        id = id,
        date = LocalDate.parse(date),
        expertId = expertId,
        expert = ExpertRegistry.get(expertId, locale).name,
        text = if (locale == AppLocale.RU) comment else commentEng,
        locationId = locationsId,
        pinned = pin == 1,
    )
}

fun VkPostDto.toDomain(): VkPostDomain {
    val colonIndex = information.indexOf(':')
    val candidate = if (colonIndex in 1..MAX_NAME_PREFIX_LENGTH) {
        information.substring(0, colonIndex).trim()
    } else null
    val userName: String
    val content: String
    if (candidate != null && NAME_PATTERN.matches(candidate)) {
        userName = candidate
        content = information.substring(colonIndex + 1).trim()
    } else {
        userName = ""
        content = information
    }
    return VkPostDomain(
        id = id,
        date = LocalDate.parse(date),
        location = location,
        userName = userName,
        content = content,
    )
}

fun MediaItemDto.toDomain(locale: AppLocale): MediaItemDomain = MediaItemDomain(
    id = id,
    date = LocalDate.parse(date),
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
    date = LocalDate.parse(date),
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
@OptIn(FormatStringsInDatetimeFormats::class)
private val format = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }

fun PinDto.toDomain(): MapPinDomain = MapPinDomain(
    date = LocalDateTime.parse(date, format),
    feeling = Feeling.fromApi(value),
    latitude = latitude,
    longitude = longitude,
    pollenType = pollenType,
    tags = tags.split(' ').filterTo(mutableSetOf()) { it.isNotEmpty() },
    friendId = friendId,
)
