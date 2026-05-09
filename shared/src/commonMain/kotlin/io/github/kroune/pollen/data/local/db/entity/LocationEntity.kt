package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name_ru") val nameRu: String = "",
    @ColumnInfo(name = "comment_ru") val commentRu: String = "",
    @ColumnInfo(name = "name_eng") val nameEng: String = "",
    @ColumnInfo(name = "comment_eng") val commentEng: String = "",
    @ColumnInfo(name = "latitude") val latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") val longitude: Double = 0.0,
)
