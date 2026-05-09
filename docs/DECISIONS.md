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
| Authentication | **Keep simple user_id model** | App is a polished wrapper around existing API; can't change server |
| VK/Facebook social feed | **Keep** | Still relevant |
| Bilingual content | **Client-side locale selection** from `_rus`/`_eng` fields | Can't change server; add in-app language setting |
| Weather provider | **Open-Meteo** | Free, no API key, good coverage |
| UI design | **Material 3 defaults** | Redesign will be done later |
| Dark Sky API | **Replaced** with Open-Meteo | Dark Sky was sunset March 2023 |

## Architecture Principles

1. **Shared-first**: maximize code in `shared/` module. Platform-specific code only for maps, platform APIs, and UI entry points.
2. **Clean layers**: `data` (DTOs, API, DB) → `domain` (models, repositories interfaces, use cases) → `presentation` (ViewModels, Compose screens).
3. **Repository pattern**: all data access goes through repositories. No direct API calls from ViewModels.
4. **DTO ↔ Domain mapping**: API DTOs are never used directly in UI. Map to domain models at the repository boundary.
5. **Error handling**: wrap network calls in `Result`. Show user-friendly errors. Retry with backoff for transient failures.
6. **Offline-aware**: Room caches all catalog data (pollens, locations, cures). Health diary entries queue locally and sync when online.

## Server API

- Base URL: `https://data.pollen.club/api_2/`
- Polygon data: `https://api.pollen.club/static/forecasts/{allergen}/{dateTime}.json`
- No authentication headers. Identity is a numeric `user_id` assigned by server on first `set_user.php` call.
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
