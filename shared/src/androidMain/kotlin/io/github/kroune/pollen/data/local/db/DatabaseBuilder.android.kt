package io.github.kroune.pollen.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private object AndroidPlatformDeps : KoinComponent {
    val context: Context get() = get()
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PollenDatabase> {
    val context = AndroidPlatformDeps.context
    val dbFile = context.getDatabasePath(PollenDatabase.DATABASE_NAME)
    return Room.databaseBuilder<PollenDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
