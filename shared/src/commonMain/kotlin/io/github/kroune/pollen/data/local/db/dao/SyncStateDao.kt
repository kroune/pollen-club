package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.SyncStateEntity

@Dao
interface SyncStateDao {
    @Query("SELECT * FROM sync_state WHERE id = 1")
    suspend fun getSyncState(): SyncStateEntity?

    @Upsert
    suspend fun upsert(state: SyncStateEntity)

    @Query("UPDATE sync_state SET last_levels_id = :id WHERE id = 1")
    suspend fun updateLastLevelsId(id: Int)

    @Query("UPDATE sync_state SET last_forecast_id = :id WHERE id = 1")
    suspend fun updateLastForecastId(id: Int)

    @Query("UPDATE sync_state SET last_statistics_id = :id WHERE id = 1")
    suspend fun updateLastStatisticsId(id: Long)

    @Query("UPDATE sync_state SET last_levels_id = 0, last_forecast_id = 0 WHERE id = 1")
    suspend fun resetLevelAndForecastIds()
}
