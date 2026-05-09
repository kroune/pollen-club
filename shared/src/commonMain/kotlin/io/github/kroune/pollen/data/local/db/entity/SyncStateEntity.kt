package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "last_levels_id") val lastLevelsId: Int = 0,
    @ColumnInfo(name = "last_forecast_id") val lastForecastId: Int = 0,
    @ColumnInfo(name = "last_statistics_id") val lastStatisticsId: Long = 0,
    @ColumnInfo(name = "last_update_timestamp") val lastUpdateTimestamp: Long = 0,
)
