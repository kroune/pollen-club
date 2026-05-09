package io.github.kroune.pollen.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelDataResponse(
    @SerialName("levels") val levels: List<LevelDto>? = null,
)

@Serializable
data class LevelForecastDataResponse(
    @SerialName("levels") val levels: List<LevelDto>? = null,
)

@Serializable
data class LevelDto(
    @SerialName("id") val id: Int,
    @SerialName("date") val date: String = "",
    @SerialName("pollen") val pollen: Int = 0,
    @SerialName("location") val location: Int = 0,
    @SerialName("value") val value: Int = 0,
)

@Serializable
data class StatisticsDataResponse(
    @SerialName("statistics") val statistics: List<StatisticDto> = emptyList(),
)

@Serializable
data class StatisticDto(
    @SerialName("id") val id: Int,
    @SerialName("date") val date: String? = null,
    @SerialName("location") val location: Int = 0,
    @SerialName("good") val good: Int = 0,
    @SerialName("middle") val middle: Int = 0,
    @SerialName("bad") val bad: Int = 0,
)

@Serializable
data class CommentsDataResponse(
    @SerialName("comments") val comments: List<CommentDto> = emptyList(),
    @SerialName("lenta_vk") val lentaVk: List<VkPostDto> = emptyList(),
    @SerialName("lenta_media") val lentaMedia: List<MediaItemDto> = emptyList(),
    @SerialName("friends") val friends: List<FriendFeelDto> = emptyList(),
)

@Serializable
data class CommentDto(
    @SerialName("id") val id: Int,
    @SerialName("date") val date: String = "",
    @SerialName("expert") val expert: String = "",
    @SerialName("comment") val comment: String = "",
    @SerialName("locations_id") val locationsId: Int = 0,
    @SerialName("comment_eng") val commentEng: String = "",
    @SerialName("pin") val pin: Int = 0,
)

@Serializable
data class VkPostDto(
    @SerialName("id") val id: Int,
    @SerialName("date") val date: String = "",
    @SerialName("location") val location: String = "",
    @SerialName("information") val information: String = "",
    @SerialName("pin") val pin: Int = 0,
)

@Serializable
data class MediaItemDto(
    @SerialName("id") val id: Int,
    @SerialName("date") val date: String = "",
    @SerialName("media_type") val mediaType: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("info_rus") val infoRus: String = "",
    @SerialName("info_eng") val infoEng: String = "",
    @SerialName("pin") val pin: Int = 0,
)

@Serializable
data class FriendFeelDto(
    @SerialName("id") val id: Int,
    @SerialName("location") val location: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("time") val time: Int = 0,
    @SerialName("friend_id") val friendId: Int = 0,
    @SerialName("opinion") val opinion: Int = 0,
)

@Serializable
data class PinsDataResponse(
    @SerialName("pins") val pins: List<PinDto> = emptyList(),
)

@Serializable
data class PinDto(
    @SerialName("date") val date: String = "",
    @SerialName("value") val value: Int = 0,
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("pollen_type") val pollenType: Int = 0,
    @SerialName("tags") val tags: String = "",
    @SerialName("friend_id") val friendId: Int = 0,
)

@Serializable
data class FriendsDataResponse(
    @SerialName("friends") val friends: List<FriendDto> = emptyList(),
)

@Serializable
data class FriendDto(
    @SerialName("id") val id: Int,
    @SerialName("friend_id") val friendId: Int = 0,
)

@Serializable
data class UserForecastDataResponse(
    @SerialName("result") val result: UserForecastResultDto? = null,
)

@Serializable
data class UserForecastResultDto(
    @SerialName("user_info") val userInfo: List<UserForecastInfoDto> = emptyList(),
    @SerialName("user_forecast") val userForecast: List<UserForecastEntryDto> = emptyList(),
)

@Serializable
data class UserForecastInfoDto(
    @SerialName("id") val id: String? = null,
    @SerialName("desc_rus") val descRus: String? = null,
    @SerialName("desc_eng") val descEng: String? = null,
)

@Serializable
data class UserForecastEntryDto(
    @SerialName("id") val id: String? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("value") val value: String? = null,
)

@Serializable
data class BannersDataResponse(
    @SerialName("banners") val banners: List<BannerDto> = emptyList(),
)

@Serializable
data class BannerDto(
    @SerialName("id") val id: Long,
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("site_url") val siteUrl: String = "",
    @SerialName("type") val type: Int = 0,
    @SerialName("start_date") val startDate: Long = 0,
    @SerialName("end_date") val endDate: Long = 0,
    @SerialName("page") val page: String = "",
    @SerialName("position") val position: Int = 0,
    @SerialName("count_in_day") val countInDay: Int = 0,
    @SerialName("duration") val duration: Int = 0,
)

@Serializable
data class PolygonDataResponse(
    @SerialName("latlngs") val latlngs: List<List<List<List<Double>>>> = emptyList(),
    @SerialName("color") val color: String = "",
    @SerialName("opacity") val opacity: Float = 0f,
    @SerialName("weight") val weight: Float = 0f,
    @SerialName("fillColor") val fillColor: String = "",
    @SerialName("fillOpacity") val fillOpacity: Float = 0f,
)
