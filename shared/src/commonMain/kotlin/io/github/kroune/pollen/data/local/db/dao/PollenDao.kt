package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.PollenEntity
import io.github.kroune.pollen.data.local.db.entity.PollenLevelInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PollenDao {
    @Upsert
    suspend fun upsertPollens(pollens: List<PollenEntity>)

    @Upsert
    suspend fun upsertLevelInfos(levelInfos: List<PollenLevelInfoEntity>)

    @Query("SELECT * FROM pollens ORDER BY id")
    fun observeAll(): Flow<List<PollenEntity>>

    @Query("SELECT * FROM pollens ORDER BY id")
    suspend fun getAll(): List<PollenEntity>

    @Query("SELECT * FROM pollen_level_info WHERE pollen_id = :pollenId ORDER BY level")
    suspend fun getLevelInfos(pollenId: Int): List<PollenLevelInfoEntity>

    @Query("SELECT * FROM pollen_level_info ORDER BY pollen_id, level")
    suspend fun getAllLevelInfos(): List<PollenLevelInfoEntity>

    @Query("DELETE FROM pollen_level_info WHERE pollen_id = :pollenId")
    suspend fun deleteLevelInfosForPollen(pollenId: Int)

    @Transaction
    suspend fun replacePollensAndLevels(
        pollens: List<PollenEntity>,
        levelInfos: List<PollenLevelInfoEntity>,
    ) {
        upsertPollens(pollens)
        for (pollen in pollens) {
            deleteLevelInfosForPollen(pollen.id)
        }
        upsertLevelInfos(levelInfos)
    }
}
