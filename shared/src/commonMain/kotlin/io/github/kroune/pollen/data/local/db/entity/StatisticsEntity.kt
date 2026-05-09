package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistics")
data class StatisticsEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val date: String? = null,
    @ColumnInfo(name = "location_id") val locationId: Int = 0,
    @ColumnInfo(name = "good") val good: Int = 0,
    @ColumnInfo(name = "middle") val middle: Int = 0,
    @ColumnInfo(name = "bad") val bad: Int = 0,
)
