package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "last_name") val lastName: String = "",
    @ColumnInfo(name = "location") val location: Int = 0,
    @ColumnInfo(name = "age") val age: Int = 0,
    @ColumnInfo(name = "activity") val activity: Int = 0,
    @ColumnInfo(name = "server_id") val serverId: Long = 0,
    @ColumnInfo(name = "selected_allergens") val selectedAllergens: String = "",
)
