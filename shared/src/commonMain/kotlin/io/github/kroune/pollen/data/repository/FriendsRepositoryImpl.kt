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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FriendsRepositoryImpl(
    private val api: PollenApiService,
    private val friendDao: FriendDao,
) : FriendsRepository {

    override fun observeFriends(): Flow<List<FriendDomain>> {
        return friendDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncFriends(userId: Long): ApiResult<Unit> = safeApiCall {
        refreshLocalFriends(userId)
    }

    override suspend fun addFriend(userId: Long, friendId: Int, localName: String): ApiResult<Unit> = safeApiCall {
        val httpResponse = api.addFriend(AddFriendRequest(userId, friendId))
        check(httpResponse.status.value in 200..299) { "Server returned ${httpResponse.status}" }
        refreshLocalFriends(userId)
        if (localName.isNotBlank()) {
            friendDao.updateName(friendId, localName)
        }
    }

    override suspend fun deleteFriend(userId: Long, friendId: Int): ApiResult<Unit> = safeApiCall {
        val httpResponse = api.deleteFriend(DeleteFriendRequest(userId, friendId))
        check(httpResponse.status.value in 200..299) { "Server returned ${httpResponse.status}" }
        friendDao.deleteByFriendId(friendId)
    }

    override suspend fun updateFriendName(friendId: Int, name: String) {
        friendDao.updateName(friendId, name)
    }

    override suspend fun getLastPinsForFriends(userId: Long): ApiResult<Map<Int, FriendLastPinDomain>> = safeApiCall {
        val response = api.getPinsWithFriends(GetUserRequest(userId))
        response.pins
            .filter { it.friendId > 0 }
            .groupBy { it.friendId }
            .mapValues { (_, pins) ->
                val latest = pins.maxBy { it.date }
                FriendLastPinDomain(
                    friendId = latest.friendId,
                    feeling = Feeling.fromApi(latest.value),
                    pollenType = latest.pollenType,
                    date = latest.date,
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
