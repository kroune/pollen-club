package io.github.kroune.pollen.data.remote.api

import io.github.kroune.pollen.data.remote.dto.request.AddFenologyRequest
import io.github.kroune.pollen.data.remote.dto.request.AddFriendRequest
import io.github.kroune.pollen.data.remote.dto.request.AddUserCureRequest
import io.github.kroune.pollen.data.remote.dto.request.AddUserFeelRequest
import io.github.kroune.pollen.data.remote.dto.request.AddUserSymptomsRequest
import io.github.kroune.pollen.data.remote.dto.request.CheckAndroidVersionRequest
import io.github.kroune.pollen.data.remote.dto.request.DeleteFriendRequest
import io.github.kroune.pollen.data.remote.dto.request.GetStatisticsRequest
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.data.remote.dto.request.LevelForecastRequest
import io.github.kroune.pollen.data.remote.dto.request.SetUserRequest
import io.github.kroune.pollen.data.remote.dto.response.AddUserFeelAndSymptomsResponse
import io.github.kroune.pollen.data.remote.dto.response.AddUserFeelResponse
import io.github.kroune.pollen.data.remote.dto.response.BannersDataResponse
import io.github.kroune.pollen.data.remote.dto.response.BaseResponse
import io.github.kroune.pollen.data.remote.dto.response.CheckAdsResponse
import io.github.kroune.pollen.data.remote.dto.response.CheckAndroidVersionResponse
import io.github.kroune.pollen.data.remote.dto.response.CommentsDataResponse
import io.github.kroune.pollen.data.remote.dto.response.CuresDataResponse
import io.github.kroune.pollen.data.remote.dto.response.FriendsDataResponse
import io.github.kroune.pollen.data.remote.dto.response.HashTagDataResponse
import io.github.kroune.pollen.data.remote.dto.response.LevelDataResponse
import io.github.kroune.pollen.data.remote.dto.response.LevelForecastDataResponse
import io.github.kroune.pollen.data.remote.dto.response.LocationDataResponse
import io.github.kroune.pollen.data.remote.dto.response.PinsDataResponse
import io.github.kroune.pollen.data.remote.dto.response.PollenDataResponse
import io.github.kroune.pollen.data.remote.dto.response.SetUserResponse
import io.github.kroune.pollen.data.remote.dto.response.StatisticsDataResponse
import io.github.kroune.pollen.data.remote.dto.response.UserForecastDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface PollenApiService {
    // POST endpoints
    suspend fun setUser(request: SetUserRequest): SetUserResponse
    suspend fun addUserFeel(request: AddUserFeelRequest): AddUserFeelResponse
    suspend fun addUserCure(request: AddUserCureRequest): AddUserFeelAndSymptomsResponse
    suspend fun addUserSymptoms(request: AddUserSymptomsRequest): AddUserFeelAndSymptomsResponse
    suspend fun addFenology(request: AddFenologyRequest): BaseResponse
    suspend fun addFriend(request: AddFriendRequest): BaseResponse
    suspend fun deleteFriend(request: DeleteFriendRequest): BaseResponse
    suspend fun checkAdsForUser(request: GetUserRequest): CheckAdsResponse
    suspend fun checkAndroidVersion(request: CheckAndroidVersionRequest): CheckAndroidVersionResponse
    suspend fun getCommentsAndLenta(request: GetUserRequest): CommentsDataResponse
    suspend fun getFriends(request: GetUserRequest): FriendsDataResponse
    suspend fun getPinsWithFriends(request: GetUserRequest): PinsDataResponse
    suspend fun getStatistics(request: GetStatisticsRequest): StatisticsDataResponse
    suspend fun getForecasts(request: LevelForecastRequest): LevelForecastDataResponse
    suspend fun getUserForecast(request: GetUserRequest): UserForecastDataResponse

    // GET endpoints
    suspend fun getLevels(fromId: Int): LevelDataResponse
    suspend fun getLocations(): LocationDataResponse
    suspend fun getPollens(): PollenDataResponse
    suspend fun getCures(): CuresDataResponse
    suspend fun getHashTags(): HashTagDataResponse
    suspend fun getScreenBanners(): BannersDataResponse
}

class PollenApiServiceImpl(
    private val client: HttpClient,
) : PollenApiService {

    private companion object {
        const val BASE_URL = "https://data.pollen.club/api_2"
    }

    override suspend fun setUser(request: SetUserRequest): SetUserResponse =
        client.post("$BASE_URL/set_user.php") { setBody(request) }.body()

    override suspend fun addUserFeel(request: AddUserFeelRequest): AddUserFeelResponse =
        client.post("$BASE_URL/add_user_feel.php") { setBody(request) }.body()

    override suspend fun addUserCure(request: AddUserCureRequest): AddUserFeelAndSymptomsResponse =
        client.post("$BASE_URL/add_user_cure_v2.php") { setBody(request) }.body()

    override suspend fun addUserSymptoms(request: AddUserSymptomsRequest): AddUserFeelAndSymptomsResponse =
        client.post("$BASE_URL/add_user_symptoms.php") { setBody(request) }.body()

    override suspend fun addFenology(request: AddFenologyRequest): BaseResponse =
        client.post("$BASE_URL/add_fenology.php") { setBody(request) }.body()

    override suspend fun addFriend(request: AddFriendRequest): BaseResponse =
        client.post("$BASE_URL/add_friend.php") { setBody(request) }.body()

    override suspend fun deleteFriend(request: DeleteFriendRequest): BaseResponse =
        client.post("$BASE_URL/delete_friend.php") { setBody(request) }.body()

    override suspend fun checkAdsForUser(request: GetUserRequest): CheckAdsResponse =
        client.post("$BASE_URL/check_ads_for_user.php") { setBody(request) }.body()

    override suspend fun checkAndroidVersion(request: CheckAndroidVersionRequest): CheckAndroidVersionResponse =
        client.post("$BASE_URL/check_android_version.php") { setBody(request) }.body()

    override suspend fun getCommentsAndLenta(request: GetUserRequest): CommentsDataResponse =
        client.post("$BASE_URL/get_comments_and_lenta.php") { setBody(request) }.body()

    override suspend fun getFriends(request: GetUserRequest): FriendsDataResponse =
        client.post("$BASE_URL/get_friends.php") { setBody(request) }.body()

    override suspend fun getPinsWithFriends(request: GetUserRequest): PinsDataResponse =
        client.post("$BASE_URL/get_pins_with_friends.php") { setBody(request) }.body()

    override suspend fun getStatistics(request: GetStatisticsRequest): StatisticsDataResponse =
        client.post("$BASE_URL/get_statistics.php") { setBody(request) }.body()

    override suspend fun getForecasts(request: LevelForecastRequest): LevelForecastDataResponse =
        client.post("$BASE_URL/get_forecasts.php") { setBody(request) }.body()

    override suspend fun getUserForecast(request: GetUserRequest): UserForecastDataResponse =
        client.post("$BASE_URL/get_user_forecast.php") { setBody(request) }.body()

    override suspend fun getLevels(fromId: Int): LevelDataResponse =
        client.get("$BASE_URL/get_levels.php") { parameter("from_id", fromId) }.body()

    override suspend fun getLocations(): LocationDataResponse =
        client.get("$BASE_URL/get_locations.php").body()

    override suspend fun getPollens(): PollenDataResponse =
        client.get("$BASE_URL/get_pollens.php").body()

    override suspend fun getCures(): CuresDataResponse =
        client.get("$BASE_URL/get_cures.php").body()

    override suspend fun getHashTags(): HashTagDataResponse =
        client.get("$BASE_URL/get_hashtags.php").body()

    override suspend fun getScreenBanners(): BannersDataResponse =
        client.get("$BASE_URL/get_screen_banners.php").body()
}
