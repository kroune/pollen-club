package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "pollen_id") val pollenId: Int = 0,
    @ColumnInfo(name = "location_id") val locationId: Int = 0,
    @ColumnInfo(name = "value") val value: Int = 0,
)

@Entity(tableName = "forecast_levels")
data class ForecastLevelEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "pollen_id") val pollenId: Int = 0,
    @ColumnInfo(name = "location_id") val locationId: Int = 0,
    @ColumnInfo(name = "value") val value: Int = 0,
)
