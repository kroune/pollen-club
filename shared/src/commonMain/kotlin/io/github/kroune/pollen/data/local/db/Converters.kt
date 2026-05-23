package io.github.kroune.pollen.data.local.db

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun fromString(value: String?): LocalDate? =
        value?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    @TypeConverter
    fun toString(date: LocalDate?): String? = date?.toString()
}
