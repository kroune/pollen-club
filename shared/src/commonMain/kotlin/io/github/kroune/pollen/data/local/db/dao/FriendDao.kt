package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Upsert
    suspend fun upsertAll(friends: List<FriendEntity>)

    @Query("SELECT * FROM friends ORDER BY id")
    fun observeAll(): Flow<List<FriendEntity>>

    @Query("DELETE FROM friends WHERE friend_id = :friendId")
    suspend fun deleteByFriendId(friendId: Int)

    @Query("DELETE FROM friends")
    suspend fun deleteAll()

    @Query("UPDATE friends SET name = :name WHERE friend_id = :friendId")
    suspend fun updateName(friendId: Int, name: String)

    @Transaction
    suspend fun replaceAll(friends: List<FriendEntity>) {
        deleteAll()
        upsertAll(friends)
    }
}
