package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Persisted user state: the server-assigned [serverId] (`null` until registered) and the locally
 * selected monitoring station ([locationId], `null` until chosen). Defaults represent the empty
 * store — this is the persistence model, distinct from the `User`/`Identity` domain model.
 */
@kotlinx.serialization.Serializable
data class UserData(
    val serverId: Long? = null,
    val locationId: Int? = null,
)

private object UserDataSerializer : OkioSerializer<UserData> {
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue = UserData()

    override suspend fun readFrom(source: BufferedSource): UserData =
        try {
            json.decodeFromString(UserData.serializer(), source.readUtf8())
        } catch (e: SerializationException) {
            defaultValue
        }

    override suspend fun writeTo(t: UserData, sink: BufferedSink) {
        sink.writeUtf8(json.encodeToString(UserData.serializer(), t))
    }
}

const val USER_DATASTORE_FILE_NAME = "pollen_user.json"

fun createUserDataStore(): DataStore<UserData> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = UserDataSerializer,
            producePath = { platformFilePath(USER_DATASTORE_FILE_NAME).toPath() },
        ),
    )
