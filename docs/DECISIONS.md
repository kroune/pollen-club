# Project Decisions

Decisions made for the Pollen Club KMP rebuild. This file is the source of truth — read it before starting any implementation session.

## Identity

| Field | Value |
|---|---|
| Package name | `io.github.kroune.pollen` |
| Platforms | Android (primary, implement first) + iOS (structure in place, implementation later) |

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin (KMP) |
| UI framework | Compose Multiplatform (Material 3 defaults) |
| Networking | Ktor client |
| Serialization | kotlinx.serialization |
| DI | Koin |
| Local DB | Room (KMP-compatible) |
| Maps (Android) | Google Maps SDK |
| Maps (iOS) | MapKit (via expect/actual, empty for now) |
| Weather API | Open-Meteo (free, no key) |
| Image loading | Coil (Compose Multiplatform compatible) |
| Navigation | Navigation 3 (`org.jetbrains.androidx.navigation3`, alpha) — uses `NavKey`, `NavDisplay`, `entryProvider`, `rememberNavBackStack`. NOT the standard `androidx.navigation:navigation-compose`. |
| Async | Kotlin Coroutines + Flow |

## Feature Decisions

| Decision | Choice | Rationale |
|---|---|---|
| Ads | **Removed entirely** | Not needed in rebuild |
| Push notifications | **Deferred** | Will add later, not in initial build |
| Authentication | **Numeric `user_id` only, owned by `UserSession`** | Server has no real auth (no token/password); `set_user.php` just mints an id. See "Identity & user state" below |
| VK/Facebook social feed | **Keep** | Still relevant |
| Bilingual content | **Client-side locale selection** from `_rus`/`_eng` fields | Can't change server; add in-app language setting |
| Weather provider | **Open-Meteo** | Free, no API key, good coverage |
| Dark Sky API | **Replaced** with Open-Meteo | Dark Sky was sunset March 2023 |

## Architecture Principles

1. **Shared-first**: maximize code in `shared/` module. Platform-specific code only for maps, platform APIs, and UI entry points.
2. **Clean layers**: `data` (DTOs, API, DB) → `domain` (models, repositories interfaces, use cases) → `presentation` (ViewModels, Compose screens).
3. **Repository pattern**: all data access goes through repositories. No direct API calls from ViewModels.
4. **DTO ↔ Domain mapping**: API DTOs are never used directly in UI. Map to domain models at the repository boundary.
5. **Error handling**: wrap network calls in `Result`. Show user-friendly errors. Retry with backoff for transient failures.
6. **Offline-aware**: Room caches all catalog data (pollens, locations, cures). Health diary entries queue locally and sync when online.

## Identity & user state (`UserSession`)

The original app had no real user profile: `set_user.php` only ever sent `id` (+ a constant `activity`); `name`/`last_name`/`ages`/`location` were hardcoded constants on the wire, never collected. The server-side identity is purely a numeric `user_id`. We model this honestly instead of carrying the original's dead `users` god-table.

- **`UserSession` is the single authority** for identity. Repositories call `session.requireUserId()` internally — there is **no `userId` parameter** on repository methods. `requireUserId()` registers with the server on first need, exactly once (mutex-guarded), and throws if it can't (offline first launch); every caller already surfaces that as a load error.
- **Identity is a sealed type**, not a sentinel: `Identity.Anonymous | Identity.Registered(serverId)`. No more `serverId == 0L` magic. `User(identity, location)` is the aggregate the app reads.
- **Location is user-domain state, not app settings.** The selected monitoring station (`User.location`) is a sibling of identity (different lifecycle — mutable, can exist while Anonymous), set locally via `UserSession.setLocation()` and **never** pushed to `set_user.php` (the original never did, and the server doesn't use it as profile). It is still passed per-request to data endpoints that take a station id.
- **Storage is a typed `DataStore<UserData>`** (`serverId: Long?`, `locationId: Int?`) via Okio + kotlinx.serialization — Room is a poor fit for a single-row singleton. The old `users` Room table is dropped (migration 5→6). `UserData`'s field defaults are the empty-store representation only; domain models (`User`/`Identity`) have **no defaults**.
- **Bootstrap** runs once from `KoinInit` (`appScope.launch { session.bootstrap() }`) as best-effort warm-up; it is decoupled from any screen (registration was previously a side-effect inside `HomeViewModel.syncData`).
- Deleted in this refactor: `UserDomain`, `UserEntity`, `UserDao`, `UserRepository`/`Impl`, `registerOrUpdateUser`, and the dead `name`/`lastName`/`age`/`activity`/`selectedAllergens` fields. `SettingsViewModel`'s "main allergen" label now reads the real `allergen_sensitivity` data instead of the perpetually-empty `selectedAllergens`.
- GPS-driven location selection is deferred — see `docs/WIP.md`.

## Server API

- Base URL: `https://data.pollen.club/api_2/`
- Polygon data: `https://api.pollen.club/static/forecasts/{allergen}/{dateTime}.json`
- No authentication headers. Identity is a numeric `user_id` assigned by server on first `set_user.php` call (managed by `UserSession`, see above).
- Full endpoint reference: `memorybank/01-api-endpoints.md`
- Full data models: `memorybank/03-data-models.md`
- Full DB schema (original): `memorybank/04-database-schema.md`

## API Keys to Carry Over

| Key | Value | Service |
|---|---|---|
| Google Maps | `AIzaSyCnVnnhlMYqql9esCIsFGjMVwiJwUj38ic` | Google Maps SDK |
| Firebase | `AIzaSyAXk57b-IQ9lZxVucJKZqCOjPvBdp-CuHg` | Firebase services |
| Firebase App ID | `1:825956231925:android:3e4c45b2206ea1adcef3bc` | Firebase |
| Firebase Project | `pollenclubandroid` | Firebase |
