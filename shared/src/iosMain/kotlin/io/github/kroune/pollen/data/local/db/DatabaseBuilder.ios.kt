package io.github.kroune.pollen.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PollenDatabase> {
    val dbFilePath = NSHomeDirectory() + "/Documents/${PollenDatabase.DATABASE_NAME}"
    return Room.databaseBuilder<PollenDatabase>(
        name = dbFilePath,
    )
}
