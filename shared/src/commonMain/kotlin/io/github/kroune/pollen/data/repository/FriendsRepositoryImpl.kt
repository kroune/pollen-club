package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.FriendDao
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.mapper.toEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.AddFriendRequest
import io.github.kroune.pollen.data.remote.dto.request.DeleteFriendRequest
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.FriendDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.FriendsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FriendsRepositoryImpl(
    private val api: PollenApiService,
    private val friendDao: FriendDao,
) : FriendsRepository {

    override fun observeFriends(): Flow<List<FriendDomain>> {
        return friendDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncFriends(userId: Long): ApiResult<Unit> = safeApiCall {
        val response = api.getFriends(GetUserRequest(userId))
        friendDao.deleteAll()
        friendDao.upsertAll(response.friends.map { it.toEntity() })
    }

    override suspend fun addFriend(userId: Long, friendId: Int): ApiResult<Unit> = safeApiCall {
        api.addFriend(AddFriendRequest(userId, friendId))
        syncFriends(userId)
    }

    override suspend fun deleteFriend(userId: Long, friendId: Int): ApiResult<Unit> = safeApiCall {
        api.deleteFriend(DeleteFriendRequest(userId, friendId))
        friendDao.deleteByFriendId(friendId)
    }
}
