package io.github.kroune.pollen.data.session

import co.touchlab.kermit.Logger
import io.github.kroune.pollen.data.local.prefs.UserData
import io.github.kroune.pollen.data.local.prefs.UserLocalDataSource
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.SetUserRequest
import io.github.kroune.pollen.domain.model.Identity
import io.github.kroune.pollen.domain.model.User
import io.github.kroune.pollen.domain.session.UserSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserSessionImpl(
    private val api: PollenApiService,
    private val local: UserLocalDataSource,
    appScope: CoroutineScope,
) : UserSession {

    private val logger = Logger.withTag("UserSession")
    private val registrationMutex = Mutex()

    override val user: StateFlow<User> =
        local.observe()
            .map { it.toUser() }
            .stateIn(appScope, SharingStarted.Eagerly, User(Identity.Anonymous, location = null))

    override suspend fun currentUser(): User = local.current().toUser()

    override suspend fun requireUserId(): Long {
        local.current().serverId?.let { return it }
        return registrationMutex.withLock {
            // Re-check inside the lock: another caller may have registered while we waited.
            local.current().serverId ?: register()
        }
    }

    override suspend fun bootstrap() {
        try {
            requireUserId()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.w(e) { "Bootstrap registration failed; will retry on demand" }
        }
    }

    override suspend fun setLocation(locationId: Int) {
        local.update { it.copy(locationId = locationId) }
    }

    /** Mints a new server id via `set_user.php` (id=0) and persists it. Caller holds the mutex. */
    private suspend fun register(): Long {
        val response = api.setUser(NEW_USER_REQUEST)
        val serverId = response.userId
        check(serverId > 0) { "Server returned invalid user id: $serverId" }
        local.update { it.copy(serverId = serverId) }
        logger.i { "Registered new user id=$serverId" }
        return serverId
    }

    private fun UserData.toUser(): User = User(
        identity = serverId?.let { Identity.Registered(it) } ?: Identity.Anonymous,
        location = locationId,
    )

    private companion object {
        // The server's set_user.php only needs id; the original app always sent these constants
        // for the remaining fields (name/last_name unused, location/ages defaults). We never
        // surface them in the domain — they exist only to satisfy the legacy wire contract.
        val NEW_USER_REQUEST = SetUserRequest(
            id = 0,
            name = "",
            lastName = "",
            location = 1,
            ages = 0,
            activity = 0,
        )
    }
}
