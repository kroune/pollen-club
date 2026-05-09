package io.github.kroune.pollen.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserRequest(
    @SerialName("user_id") val userId: Long,
)

@Serializable
data class SetUserRequest(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("location") val location: Int,
    @SerialName("ages") val ages: Int,
    @SerialName("activity") val activity: Int,
)

@Serializable
data class AddUserFeelRequest(
    @SerialName("date") val date: String,
    @SerialName("location") val location: Int,
    @SerialName("time") val time: Long,
    @SerialName("opinion") val opinion: Int,
    @SerialName("opinion_old") val opinionOld: Int,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("user_id") val userId: Long,
    @SerialName("default_pollen") val defaultPollen: Int,
    @SerialName("tags") val tags: String,
    @SerialName("location_name") val locationName: String,
)

@Serializable
data class SymptomEntryDto(
    @SerialName("date") val date: String,
    @SerialName("tags") val tags: String,
    @SerialName("nose") val nose: Int,
    @SerialName("throat") val throat: Int,
    @SerialName("eyes") val eyes: Int,
    @SerialName("general") val general: Int,
    @SerialName("lunds") val lungs: Int,
    @SerialName("other") val other: String,
)

@Serializable
data class AddUserSymptomsRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("symptoms") val symptoms: List<SymptomEntryDto>,
)

@Serializable
data class AddUserCureRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("date") val date: String,
    @SerialName("cure_id") val cureId: Int,
    @SerialName("cure_name") val cureName: String,
    @SerialName("forma_id") val formaId: Int,
    @SerialName("forma") val forma: String,
    @SerialName("frequency_id") val frequencyId: Int,
    @SerialName("frequency") val frequency: String,
    @SerialName("use_from") val useFrom: String,
    @SerialName("dose_id") val doseId: Int,
    @SerialName("dose") val dose: String,
    @SerialName("dose_value") val doseValue: Int,
    @SerialName("frequency_value") val frequencyValue: Int,
    @SerialName("type") val type: Int,
)

@Serializable
data class AddFenologyRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("date") val date: String,
    @SerialName("time") val time: Long,
    @SerialName("comment") val comment: String,
    @SerialName("state") val state: Int,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
)

@Serializable
data class AddFriendRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("friend_id") val friendId: Int,
)

@Serializable
data class DeleteFriendRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("friend_id") val friendId: Int,
)

@Serializable
data class CheckAndroidVersionRequest(
    @SerialName("build") val build: Int,
)

@Serializable
data class GetStatisticsRequest(
    @SerialName("from_id") val fromId: Long,
)

@Serializable
data class LevelForecastRequest(
    @SerialName("from_id") val fromId: Int,
)
