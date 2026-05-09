package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.ForecastLevelEntity
import io.github.kroune.pollen.data.local.db.entity.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Upsert
    suspend fun upsertLevels(levels: List<LevelEntity>)

    @Query("SELECT * FROM levels WHERE location_id = :locationId AND date = :date ORDER BY pollen_id")
    suspend fun getByLocationAndDate(locationId: Int, date: String): List<LevelEntity>

    @Query("SELECT * FROM levels WHERE location_id = :locationId ORDER BY date DESC, pollen_id")
    fun observeByLocation(locationId: Int): Flow<List<LevelEntity>>

    @Query("SELECT MAX(id) FROM levels")
    suspend fun getMaxId(): Int?

    @Upsert
    suspend fun upsertForecasts(forecasts: List<ForecastLevelEntity>)

    @Query("SELECT * FROM forecast_levels WHERE location_id = :locationId AND date = :date ORDER BY pollen_id")
    suspend fun getForecastsByLocationAndDate(locationId: Int, date: String): List<ForecastLevelEntity>

    @Query("SELECT * FROM forecast_levels WHERE location_id = :locationId ORDER BY date DESC, pollen_id")
    fun observeForecastsByLocation(locationId: Int): Flow<List<ForecastLevelEntity>>

    @Query("SELECT MAX(id) FROM forecast_levels")
    suspend fun getMaxForecastId(): Int?

    @Query("SELECT * FROM forecast_levels WHERE location_id = :locationId AND pollen_id = :pollenId ORDER BY date ASC")
    suspend fun getForecastsByLocationAndPollen(locationId: Int, pollenId: Int): List<ForecastLevelEntity>

    @Query("SELECT * FROM levels WHERE location_id = :locationId AND pollen_id = :pollenId ORDER BY date ASC")
    suspend fun getLevelsByLocationAndPollen(locationId: Int, pollenId: Int): List<LevelEntity>
}
