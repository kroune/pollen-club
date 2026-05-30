package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.FriendDao
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.AddFriendRequest
import io.github.kroune.pollen.data.remote.dto.request.DeleteFriendRequest
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.FriendDomain
import io.github.kroune.pollen.domain.model.FriendLastPinDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.session.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class FriendsRepositoryImpl(
    private val api: PollenApiService,
    private val friendDao: FriendDao,
    private val session: UserSession,
) : FriendsRepository {

    override fun observeFriends(): Flow<List<FriendDomain>> {
        return friendDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncFriends(): ApiResult<Unit> = safeApiCall {
        refreshLocalFriends(session.requireUserId())
    }

    override suspend fun addFriend(friendId: Int, localName: String): ApiResult<Unit> = safeApiCall {
        val userId = session.requireUserId()
        val httpResponse = api.addFriend(AddFriendRequest(userId, friendId))
        check(httpResponse.status.value in 200..299) { "Server returned ${httpResponse.status}" }
        refreshLocalFriends(userId)
        if (localName.isNotBlank()) {
            friendDao.updateName(friendId, localName)
        }
    }

    override suspend fun deleteFriend(friendId: Int): ApiResult<Unit> = safeApiCall {
        val httpResponse = api.deleteFriend(DeleteFriendRequest(session.requireUserId(), friendId))
        check(httpResponse.status.value in 200..299) { "Server returned ${httpResponse.status}" }
        friendDao.deleteByFriendId(friendId)
    }

    override suspend fun updateFriendName(friendId: Int, name: String) {
        friendDao.updateName(friendId, name)
    }

    override suspend fun getLastPinsForFriends(): ApiResult<Map<Int, FriendLastPinDomain>> = safeApiCall {
        val response = api.getPinsWithFriends(GetUserRequest(session.requireUserId()))
        response.pins
            .filter { it.friendId > 0 }
            .groupBy { it.friendId }
            .mapValues { (_, pins) ->
                val latest = pins.maxBy { it.date }
                FriendLastPinDomain(
                    friendId = latest.friendId,
                    feeling = Feeling.fromApi(latest.value),
                    pollenType = latest.pollenType,
                    date = LocalDate.parse(latest.date),
                )
            }
    }

    private suspend fun refreshLocalFriends(userId: Long) {
        val response = api.getFriends(GetUserRequest(userId))
        val existingNames = friendDao.observeAll().first().associate { it.friendId to it.name }
        friendDao.replaceAll(response.friends.map { dto ->
            dto.toEntity().copy(name = existingNames[dto.friendId] ?: "")
        })
    }
}
