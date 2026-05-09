package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "therapies")
data class TherapyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "cure_type_id") val cureTypeId: Int = 0,
    @ColumnInfo(name = "cure_name") val cureName: String = "",
    @ColumnInfo(name = "cure_id") val cureId: Int = 0,
    @ColumnInfo(name = "form") val form: String = "",
    @ColumnInfo(name = "form_id") val formId: Int = 0,
    @ColumnInfo(name = "dose") val dose: String = "",
    @ColumnInfo(name = "dose_id") val doseId: Int = 0,
    @ColumnInfo(name = "dose_value") val doseValue: Int = 0,
    @ColumnInfo(name = "frequency") val frequency: String = "",
    @ColumnInfo(name = "frequency_id") val frequencyId: Int = 0,
    @ColumnInfo(name = "frequency_value") val frequencyValue: Int = 0,
    @ColumnInfo(name = "start_date") val startDate: String = "",
)
