package io.github.kroune.pollen.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    @SerialName("result") val result: Boolean = false,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class SetUserResponse(
    @SerialName("result") val result: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("user_id") val userId: Long = 0,
)

@Serializable
data class CheckAdsResponse(
    @SerialName("result") val result: Boolean = false,
    @SerialName("show_banners") val showBanners: Boolean = false,
    @SerialName("show_counter") val showCounter: Int = 5,
)

@Serializable
data class CheckAndroidVersionResponse(
    @SerialName("is_old_version") val isOldVersion: Boolean = false,
)

@Serializable
data class AddUserFeelResponse(
    @SerialName("result") val result: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("user_id") val userId: Int = 0,
    @SerialName("statistics") val statistics: StatisticDto? = null,
)

@Serializable
data class AddUserFeelAndSymptomsResponse(
    @SerialName("result") val result: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("user_id") val userId: Long = 0,
)
