package io.github.kroune.pollen.domain.session

import io.github.kroune.pollen.domain.model.User
import kotlinx.coroutines.flow.StateFlow

/**
 * Single authority for the user's server identity and selected location.
 *
 * Replaces the old `UserRepository`/`UserDao`/`UserEntity` profile layer. Identity is obtained
 * lazily and exactly once via [requireUserId]; data repositories call that internally instead of
 * threading a `userId` through every method.
 */
interface UserSession {

    /**
     * Reactive identity + selected location. Always has a current value (seeded
     * [io.github.kroune.pollen.domain.model.Identity.Anonymous] until storage is read).
     * Use for UI that should update when registration completes or the station changes.
     */
    val user: StateFlow<User>

    /**
     * Latest persisted snapshot, read straight from storage (not the [user] StateFlow seed).
     * Use when a one-shot read must reflect the stored value at call time.
     */
    suspend fun currentUser(): User

    /**
     * Returns the server-assigned user id, registering with the server on first need.
     * Registration runs at most once even under concurrent callers. Throws if no id exists yet
     * and registration fails (e.g. offline on first launch) — callers surface that as a load error.
     */
    suspend fun requireUserId(): Long

    /** Best-effort registration warm-up, fired once at app start. Never throws. */
    suspend fun bootstrap()

    /** Updates the locally selected monitoring station. Local only — not pushed to the server. */
    suspend fun setLocation(locationId: Int)
}
