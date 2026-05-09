package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.AllergenSensitivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AllergenSensitivityDao {

    @Upsert
    suspend fun upsert(sensitivity: AllergenSensitivityEntity)

    @Upsert
    suspend fun upsertAll(sensitivities: List<AllergenSensitivityEntity>)

    @Query("SELECT * FROM allergen_sensitivity")
    fun observeAll(): Flow<List<AllergenSensitivityEntity>>

    @Query("SELECT * FROM allergen_sensitivity")
    suspend fun getAll(): List<AllergenSensitivityEntity>

    @Query("SELECT * FROM allergen_sensitivity WHERE pollen_id = :pollenId")
    suspend fun getByPollenId(pollenId: Int): AllergenSensitivityEntity?

    @Query("DELETE FROM allergen_sensitivity WHERE pollen_id = :pollenId")
    suspend fun delete(pollenId: Int)
}
