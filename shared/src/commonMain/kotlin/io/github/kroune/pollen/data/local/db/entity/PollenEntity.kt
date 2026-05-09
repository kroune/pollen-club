package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pollens")
data class PollenEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "desc_ru") val descRu: String = "",
    @ColumnInfo(name = "desc_eng") val descEng: String = "",
    @ColumnInfo(name = "info_ru") val infoRu: String = "",
    @ColumnInfo(name = "info_eng") val infoEng: String = "",
    @ColumnInfo(name = "max_level") val maxLevel: Int = 0,
)

@Entity(tableName = "pollen_level_info")
data class PollenLevelInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "pollen_id") val pollenId: Int,
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name = "name_ru") val nameRu: String = "",
    @ColumnInfo(name = "name_eng") val nameEng: String = "",
    @ColumnInfo(name = "info_ru") val infoRu: String = "",
    @ColumnInfo(name = "info_eng") val infoEng: String = "",
    @ColumnInfo(name = "color") val color: Int = 0,
)
