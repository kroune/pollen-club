package io.github.kroune.pollen.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PollenDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), PollenDatabase.DATABASE_NAME)
    return Room.databaseBuilder<PollenDatabase>(
        name = dbFile.absolutePath,
    )
}