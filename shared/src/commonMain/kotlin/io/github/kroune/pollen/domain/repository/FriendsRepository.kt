package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.FriendDomain
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {
    fun observeFriends(): Flow<List<FriendDomain>>
    suspend fun syncFriends(userId: Long): ApiResult<Unit>
    suspend fun addFriend(userId: Long, friendId: Int): ApiResult<Unit>
    suspend fun deleteFriend(userId: Long, friendId: Int): ApiResult<Unit>
}
