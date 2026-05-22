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

- **MVI** — every screen has `State`, sealed `Intent`, and `UiEvent` effects via the `MviViewModel` base class.
- **Repository pattern** — all data access through repository interfaces (domain/) with implementations (data/).
- **DTO ↔ Domain mapping** — never use API DTOs in UI. Map at the repository boundary.
- **Locale resolution** — server sends `_rus`/`_eng` field pairs. Mappers receive `AppLocale` to pick the right one. The UI does **not** know about locale — moko-resources handles all UI string localization via `applyMokoLocale(locale)` called once from `KoinInit`.
- **Dates are `LocalDate`** end-to-end — domain, Room (via `LocalDateConverter`), DAOs. Never bridge through `String`. Format for UI via `formatDateLocalized(date)`.
- **Incremental sync** — levels, forecasts, statistics use `from_id` pattern. Store last ID in Room.
- **Local-first for health entries** — save to Room immediately, sync to server in background.
- **expect/actual for platform features** — `PlatformMapView` (maps), `rememberCopyToClipboard`, `rememberShareTextLauncher`. Pattern: one `expect` in commonMain, `actual` in androidMain/iosMain/jvmMain.

## MVI ViewModel Pattern

Every ViewModel extends `MviViewModel<State, Intent, Effect>`. Do not roll your own `_state` / `_events` plumbing.

### Base class (do not change without good reason)

```kotlin
abstract class MviViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {
    val state: StateFlow<State>
    val effects: Flow<Effect>
    protected val currentState: State
    fun onIntent(intent: Intent)
    protected abstract fun handleIntent(intent: Intent)
    protected fun updateState(reducer: State.() -> State)
    protected fun emitEffect(effect: Effect)
}
```

### Concrete ViewModel shape

```kotlin
@Stable
data class FooUiState(
    val items: LoadState<ImmutableList<Item>> = LoadState.Loading,
    val query: String = "",
)

sealed interface FooIntent {
    data object LoadData : FooIntent
    data class SearchQueryChanged(val query: String) : FooIntent
}

class FooViewModel(private val repo: FooRepository) :
    MviViewModel<FooUiState, FooIntent, UiEvent>(FooUiState()) {

    init { observeItems() }

    override fun handleIntent(intent: FooIntent) {
        when (intent) {
            FooIntent.LoadData -> reload()
            is FooIntent.SearchQueryChanged -> updateState { copy(query = intent.query) }
        }
    }

    private fun observeItems() {
        viewModelScope.launch {
            repo.observeItems().collect { items ->
                updateState { copy(items = LoadState.Loaded(items.toImmutableList())) }
            }
        }
    }
}
```

### State slices — independent observers, no mega-combine

When state has multiple async fields with **different** dependency sets, split them into independent slice observers. Each slice has its own `viewModelScope.launch { sourceFlow.collect { updateState { copy(slice = …) } } }`.

Rules:
- **If slices share inputs and outputs depend on them together** → use one `combine` (e.g. `userAllergens` needs pollens + sensitivities + levels, all three together).
- **If slices have independent inputs** → separate `observe<X>()` private functions, one launch each. Don't merge with `combine` just to centralize; that re-runs every output when any input changes.
- **Intent-only state** (search query, sheet expanded, dialog visibility) → no slice observer, write to state directly from `handleIntent` via `updateState`.
- **No final assembly combine.** Slices update their own fields independently — Compose recomposes on each `updateState`.
- **Never add helpers to `MviViewModel`** to "simplify" the launch/collect/updateState pattern. The pattern is 3 lines per slice; abstractions there grow the base class API surface for marginal gain.

Example with 3 independent slices (Phenology):
```kotlin
init {
    observeStages()        // observations + locale → stages, currentStageLabel
    observeAllergenName()  // pollens → allergenName
    observeLocationLabel() // user + locations → locationLabel
}
```

### One-shot effects — `emitEffect` + `Flow<UiEvent>`

`UiEvent.ShowError(message: StringDesc)` is the only event type. Emit it for transient errors:

```kotlin
emitEffect(UiEvent.ShowError(MR.strings.error_load_data.desc()))
```

`MviViewModel.effects` is `Flow<UiEvent>`. The screen wires it to a snackbar via `CollectEffects`.

### Error handling

```kotlin
try {
    // …
} catch (e: CancellationException) {
    throw e                              // ALWAYS rethrow — never swallow coroutine cancellation
} catch (e: Exception) {
    updateState { copy(items = LoadState.Failed) }
    emitEffect(UiEvent.ShowError(MR.strings.error_load_items.desc()))
}
```

Rules:
- Always rethrow `CancellationException` before any general `catch (e: Exception)`.
- Show `LoadState.Failed` AND `emitEffect(UiEvent.ShowError(…))` — both, not just one.
- Error messages must be user-friendly `StringDesc` from `MR.strings.*`, never raw exception text.
- Reactive `observe*()` flows from repos generally don't throw; defensive `.catch` is unnecessary unless you have a real error source.

## Screen Pattern

Each navigation-facing composable is a **single function** that takes state/effects/onIntent + navigation lambdas. There is no wrapper that injects the ViewModel — the entry block in `App.kt` does that.

```kotlin
@Composable
fun FooScreen(
    state: FooUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onIntent: (FooIntent) -> Unit = {},
    onNavigateToBar: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState, onRetry = { onIntent(FooIntent.LoadData) })

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        when (val data = state.items) {
            is LoadState.Loading -> /* skeleton */
            is LoadState.Loaded  -> /* content */
            is LoadState.Failed  -> FullScreenError(onRetry = { onIntent(FooIntent.LoadData) })
        }
    }
}
```

Rules:
- **No `viewModel:` parameter.** Ever. VMs are created in `App.kt`'s `entryProvider`.
- **`effects` and `onIntent` have defaults** (`emptyFlow()` and `{}`) so previews work with just `state`.
- **`CollectEffects` lives inside the screen**, not in any wrapper.
- **Local UI state** (dialogs, `LaunchedEffect`, `mutableStateOf`) goes inside this single screen function. Don't add another wrapper to "host" it.
- **For 3+ intent-mapped callbacks**, pass `onIntent: (Intent) -> Unit` instead of individual lambdas. Navigation lambdas stay separate.
- **Snackbar errors only for transient failures.** When all critical data failed, show `FullScreenError`. Inline `ErrorBanner` for partial failures.

## Navigation Pattern

Each `entry<XRoute>` in `App.kt` is responsible for creating its ViewModel and passing state/effects/onIntent to the screen:

```kotlin
entry<FooRoute> {
    val vm: FooViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    Box(Modifier.padding(innerPadding).fillMaxSize()) {
        FooScreen(
            state = state,
            effects = vm.effects,
            onIntent = vm::onIntent,
            onNavigateToBar = { backStack.add(BarRoute) },
        )
    }
}
```

For routes with parameters (`ForecastDetailRoute(pollenId)`):
```kotlin
entry<ForecastDetailRoute> { route ->
    val vm = koinViewModel<ForecastDetailViewModel>(
        key = "forecast_${route.pollenId}",
    ) { parametersOf(route.pollenId) }
    val state by vm.state.collectAsState()
    // …
}
```

For routes that embed multiple VMs (`FeedRoute` embeds friends content), create both VMs in the entry block and pass slices to the screen.

## Common UI Components (do not recreate)

All in `presentation/common/`:
- `CollectEffects(effects, snackbarHostState, onRetry?)` — wires `Flow<UiEvent>` to Snackbar
- `FullScreenError(onRetry)` — centered error + retry button, use when entire screen fails
- `ErrorBanner(onRetry)` — inline card-style banner for partial failures
- `ShimmerEffect` — modifier extension for skeleton shimmer animation
- `rememberCopyToClipboard(): (String) -> Unit` — expect/actual clipboard write
- `rememberShareTextLauncher(): (String) -> Unit` — expect/actual share sheet
- `formatDateLocalized(date: LocalDate): String` — moko-resources backed date formatting
- Per-section skeletons (`*Skeleton`) for loading states — grep `presentation/common/` for the one you need

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
- Use `kotlin.time.Clock.System.now()` and `kotlin.time.Instant`, NOT `kotlinx.datetime.Clock` / `kotlinx.datetime.Instant` (the latter are deprecated typealiases as of kotlinx-datetime 0.7).
- For month-of-year as 1-12 use `date.month.number`. For day-of-month use `date.day` (not deprecated `.dayOfMonth`).
- `AppLocale` has `tag: String` and `fromTag(String): AppLocale` — use these for storage round-trips, never hardcode `"ru"` / `"en"` literals.
- `applyMokoLocale(locale)` is called once from `KoinInit`. UI code reads strings via `stringResource(MR.strings.*)` or `StringDesc.localized()` — never inject `LocaleProvider` into composables.

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
