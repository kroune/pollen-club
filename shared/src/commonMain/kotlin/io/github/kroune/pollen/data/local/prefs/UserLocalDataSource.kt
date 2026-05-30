package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/** Single storage gateway for [UserData]. Replaces the old Room `users` table + `UserDao`. */
class UserLocalDataSource(
    private val dataStore: DataStore<UserData>,
) {
    fun observe(): Flow<UserData> = dataStore.data

    suspend fun current(): UserData = dataStore.data.first()

    suspend fun update(transform: (UserData) -> UserData) {
        dataStore.updateData(transform)
    }
}
