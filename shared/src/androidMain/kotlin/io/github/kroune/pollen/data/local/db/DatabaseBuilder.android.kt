package io.github.kroune.pollen.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PollenDatabase> {
    val dbFile = appContext.getDatabasePath(PollenDatabase.DATABASE_NAME)
    return Room.databaseBuilder<PollenDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
