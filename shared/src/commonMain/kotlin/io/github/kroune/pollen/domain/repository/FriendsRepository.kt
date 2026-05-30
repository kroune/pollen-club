package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.FriendDomain
import io.github.kroune.pollen.domain.model.FriendLastPinDomain
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {
    fun observeFriends(): Flow<List<FriendDomain>>
    suspend fun syncFriends(): ApiResult<Unit>
    suspend fun addFriend(friendId: Int, localName: String): ApiResult<Unit>
    suspend fun deleteFriend(friendId: Int): ApiResult<Unit>
    suspend fun updateFriendName(friendId: Int, name: String)
    suspend fun getLastPinsForFriends(): ApiResult<Map<Int, FriendLastPinDomain>>
}
