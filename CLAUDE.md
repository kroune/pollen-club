# Pollen Club — KMP Rebuild

## What This Project Is

A Kotlin Multiplatform rebuild of Pollen Club, a pollen monitoring app for allergy sufferers. We're rebuilding from a decompiled APK analysis (in `memorybank/`) into a clean KMP codebase. The original app had severe quality issues (god activity, no DI, no error handling, raw SQLite, dead APIs).

Package: `io.github.kroune.pollen`

## Before You Start

1. Read `docs/DECISIONS.md` — all tech stack and feature decisions
2. Read `docs/ARCHITECTURE.md` — module structure, data flow, patterns
3. Read `docs/PLAN.md` — phased implementation plan with checkboxes
4. Check which phases are already complete (look for `[x]` in `PLAN.md`)
5. Read the relevant `memorybank/` files listed in your phase's "Context files"

## Tech Stack

- **KMP** — shared module + androidApp + iosApp
- **Compose Multiplatform** — UI (Material 3)
- **Ktor** — HTTP client
- **kotlinx.serialization** — JSON
- **Koin** — dependency injection
- **Room** — local database (KMP-compatible)
- **Coil** — image loading
- **Google Maps** (Android) / MapKit (iOS) — maps via expect/actual
- **Open-Meteo** — weather (replaces dead Dark Sky API)
- **Vico** (`com.patrykandpatrick.vico`) — charting library for pollen level graphs
- **DataStore** — preferences
- **Navigation 3** (`org.jetbrains.androidx.navigation3`, alpha) — `NavKey`/`NavDisplay`/`entryProvider`/`rememberNavBackStack`. This is NOT standard `androidx.navigation:navigation-compose`. Do not import from the wrong artifact.
- **Kermit** (`co.touchlab.kermit`) — structured logging. Use `Logger.withTag("Tag")` instead of `println` or `Log`.
- **kotlinx-collections-immutable** — `ImmutableList`/`persistentListOf` for all list fields in UiState to prevent unnecessary recomposition

## Project Structure

```
shared/src/commonMain/kotlin/io/github/kroune/pollen/
├── data/
│   ├── local/db/        # Room database, DAOs (dao/), entities (entity/)
│   ├── local/prefs/     # DataStore preferences
│   ├── mapper/          # DTO ↔ Domain mappers
│   ├── remote/api/      # Ktor API service classes
│   ├── remote/dto/      # Request and response DTOs (request/, response/)
│   ├── remote/weather/  # Open-Meteo weather API client
│   └── repository/      # Repository implementations
├── domain/              # Domain models, repository interfaces, use cases
├── presentation/        # ViewModels and Compose screens per feature
├── di/                  # Koin modules
└── util/                # Utilities
androidApp/         # Android entry point (MainActivity, manifest, resources)
iosApp/             # iOS entry point (stub for now)
memorybank/         # Original app analysis (10 reference docs, read-only)
docs/               # Planning docs (DECISIONS.md, ARCHITECTURE.md, PLAN.md)
```

## Key Patterns

- **Repository pattern** — all data access through repository interfaces (domain/) with implementations (data/)
- **DTO ↔ Domain mapping** — never use API DTOs in UI. Map at the repository boundary.
- **Locale resolution** — server sends `_rus`/`_eng` field pairs. Mappers take `AppLocale` to pick the right one. User can switch language in settings.
- **Incremental sync** — levels, forecasts, statistics use `from_id` pattern. Store last ID in Room.
- **Local-first for health entries** — save to Room immediately, sync to server in background.
- **expect/actual for maps** — `PlatformMapView` composable with Google Maps on Android, MapKit on iOS.

## ViewModel & UI Patterns

Every screen follows the same shape. Do not deviate from it.

### UiState — LoadState + ImmutableList

```kotlin
data class FooUiState(
    val items: LoadState<ImmutableList<Item>> = LoadState.Loading,
    val isRefreshing: Boolean = false,
)
```

`LoadState<T>` is a sealed interface with `Loading`, `Loaded<T>`, `Failed`. Use it for every async field.
Use `ImmutableList` (not `List`) for all list fields in UiState.

### One-shot Events — Channel

```kotlin
private val _events = Channel<UiEvent>(Channel.BUFFERED)
val events = _events.receiveAsFlow()
```

`UiEvent.ShowError(message)` is the only event type. Send it for transient errors. Use `CollectEvents` in the screen:

```kotlin
CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)
```

### Error Handling in ViewModels

```kotlin
try {
    // ...
} catch (e: CancellationException) {
    throw e                         // ALWAYS rethrow — never swallow coroutine cancellation
} catch (e: Exception) {
    _state.value = _state.value.copy(data = LoadState.Failed)
    _events.send(UiEvent.ShowError("Friendly user-facing message"))
}
```

Rules:
- **Always rethrow `CancellationException`** before any `catch (e: Exception)` block.
- Show `LoadState.Failed` in state AND send a `UiEvent.ShowError` — both, not just one.
- Error messages sent via `UiEvent.ShowError` must be **user-friendly** ("Failed to load pollen data"), never raw exception messages or stack traces.
- Do not silently ignore failures. Do not show only a log and move on.

### Screen Structure

```kotlin
@Composable
fun FooScreen(viewModel: FooViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        when (val data = state.items) {
            is LoadState.Loading -> /* skeleton */
            is LoadState.Loaded  -> /* content */
            is LoadState.Failed  -> FullScreenError(onRetry = viewModel::loadData)
        }
    }
}
```

When multiple data pieces can partially fail: show `FullScreenError` only if **all** critical data failed; show `ErrorBanner` inline if only some data failed.

## Common UI Components (do not recreate)

All in `presentation/common/`:
- `CollectEvents(events, snackbarHostState, onRetry?)` — wires UiEvent channel to Snackbar
- `FullScreenError(onRetry)` — centered error + retry button, use when entire screen fails
- `ErrorBanner(onRetry)` — inline card-style banner for partial failures
- `ShimmerEffect` — modifier extension for skeleton shimmer animation
- Skeleton composables: `LocationHeaderSkeleton`, `WeatherCardSkeleton`, `PollenListSkeleton`, `PollenLevelCardSkeleton`, `FeedCardSkeleton`, `FeedListSkeleton`, `MedicationCardSkeleton`, `MedicationListSkeleton`, `CategoriesCardSkeleton`, `FriendsListSkeleton`, `MapChipRowSkeleton`, `MapAreaSkeleton`, `DayStripSkeleton`, `PersonalIndexCardSkeleton`, `ForecastDetailHeaderSkeleton`, `ForecastDetailChartSkeleton`, `ForecastDetailStatsSkeleton`

Before writing any new common component, check this directory first.

## Server API

- Base URL: `https://data.pollen.club/api_2/`
- No authentication — identity is a numeric `user_id` assigned by server on first call
- Full reference: `memorybank/01-api-endpoints.md`
- We cannot change the server. The app is a client-only rebuild.

## Conventions

- Kotlin, modern idioms (sealed classes, data classes, extension functions, coroutines + Flow)
- kotlinx.serialization `@SerialName` must match server field names exactly (including typos like `"lunds"` for lungs)
- Material 3 defaults for UI — no custom design system yet
- No ads in the rebuild
- Android-first implementation, iOS structure in place but `actual` implementations can be stubs
- Use `kotlin.time.Clock.System.now()` for current time, NOT `kotlinx.datetime.Clock.System.now()`. The correct import is `kotlin.time.Clock`.

## Build & Run

```bash
./gradlew :androidApp:assembleDebug                           # Build Android
./gradlew :shared:compileKotlinIosArm64                        # Compile shared for iOS
./gradlew :androidApp:testDebugUnitTest -PexcludeScreenshots   # Run Android unit tests (no screenshots)
./gradlew :androidApp:recordRoborazziDebug                     # Record screenshot baselines (Roborazzi)
./gradlew :androidApp:verifyRoborazziDebug                     # Verify screenshots against baselines
./gradlew clean                                                # Clean build artifacts
```

## UI Development Workflow

- Design reference files are provided at `design/polished/` (use the polished version)
- Always compare implementation against the design on a real device
- Do not declare UI work done without visual verification via screenshot

## When You Complete a Phase

1. Mark all completed tasks as `[x]` in `docs/PLAN.md`
2. If you made architectural decisions not covered in `docs/DECISIONS.md`, add them
3. If the actual structure diverged from `docs/ARCHITECTURE.md`, update it
