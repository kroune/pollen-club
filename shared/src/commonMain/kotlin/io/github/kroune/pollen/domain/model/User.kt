package io.github.kroune.pollen.domain.model

/**
 * Server-assigned identity. The Pollen Club server mints a numeric id on first contact
 * (`set_user.php` with `id=0`); there are no credentials, tokens, or passwords — the id is
 * the whole of the server-side identity.
 */
sealed interface Identity {
    data object Anonymous : Identity
    data class Registered(val serverId: Long) : Identity
}

/**
 * The current user: a server [identity] plus the locally selected monitoring station ([location]).
 *
 * Location is user-domain state, not server identity — the backend never stored it on the user
 * record — but it travels with the user across the app, so it lives here rather than in app settings.
 * `null` location means none has been chosen yet.
 */
data class User(
    val identity: Identity,
    val location: Int?,
)

/** The server id if registered, or `null` while [Identity.Anonymous]. */
val Identity.serverIdOrNull: Long?
    get() = (this as? Identity.Registered)?.serverId
