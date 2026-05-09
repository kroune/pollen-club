package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "friend_id") val friendId: Int = 0,
)
