package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.MedicationIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationIntakeDao {

    @Upsert
    suspend fun upsert(intake: MedicationIntakeEntity)

    @Query("SELECT * FROM medication_intakes WHERE therapy_id = :therapyId AND date = :date LIMIT 1")
    suspend fun getByTherapyAndDate(therapyId: Long, date: String): MedicationIntakeEntity?

    @Query("SELECT * FROM medication_intakes WHERE date = :date")
    fun observeByDate(date: String): Flow<List<MedicationIntakeEntity>>

    @Query("SELECT * FROM medication_intakes WHERE therapy_id = :therapyId ORDER BY date DESC")
    fun observeByTherapy(therapyId: Long): Flow<List<MedicationIntakeEntity>>
}
