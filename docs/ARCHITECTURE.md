# Architecture

## Module Structure

```
PolenClub/
├── shared/                          # KMP shared module
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/io/github/kroune/pollen/
│       │       ├── data/
│       │       │   ├── remote/
│       │       │   │   ├── api/          # Ktor API service definitions
│       │       │   │   ├── dto/          # kotlinx.serialization DTOs (match server JSON exactly)
│       │       │   │   └── weather/      # Open-Meteo client
│       │       │   ├── local/
│       │       │   │   ├── db/           # Room database, entities, DAOs
│       │       │   │   └── prefs/        # DataStore preferences
│       │       │   ├── repository/       # Repository implementations
│       │       │   └── mapper/           # DTO <-> Domain mappers
│       │       ├── domain/
│       │       │   ├── model/            # Domain models (UI-ready)
│       │       │   ├── repository/       # Repository interfaces
│       │       │   └── usecase/          # Use cases (only where logic warrants it)
│       │       ├── presentation/
│       │       │   ├── home/             # Dashboard screen
│       │       │   ├── map/              # Map screen (shared logic, platform map via expect/actual)
│       │       │   ├── diary/            # Health diary (feel + symptoms)
│       │       │   ├── medications/      # Medication tracking
│       │       │   ├── feed/             # Expert comments + VK + media feed
│       │       │   ├── phenology/        # Phenology observations
│       │       │   ├── friends/          # Friends management
│       │       │   ├── settings/         # Profile, allergens, locations, language
│       │       │   └── common/           # Shared UI components
│       │       ├── di/                   # Koin modules
│       │       ├── util/                 # Utilities (locale, date, etc.)
│       │       └── App.kt               # Shared Compose entry point
│       ├── commonTest/                   # Shared tests
│       ├── androidMain/
│       │   └── kotlin/io/github/kroune/pollen/
│       │       ├── map/                  # Google Maps composable (actual)
│       │       ├── di/                   # Android-specific Koin bindings
│       │       └── Platform.android.kt
│       └── iosMain/
│           └── kotlin/io/github/kroune/pollen/
│               ├── map/                  # MapKit composable (actual, stub for now)
│               ├── di/                   # iOS-specific Koin bindings
│               └── Platform.ios.kt
├── androidApp/                      # Android application module
│   ├── src/main/
│   │   ├── kotlin/io/github/kroune/pollen/
│   │   │   └── MainActivity.kt      # Single activity, setContent { App() }
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── google-services.json
├── iosApp/                          # iOS application (Xcode project)
│   └── (Xcode project files, empty implementation for now)
├── memorybank/                      # Original app analysis (reference only)
├── docs/                            # Project planning documents
│   ├── DECISIONS.md                 # All decisions
│   ├── ARCHITECTURE.md              # This file
│   └── PLAN.md                      # Implementation plan with phases
├── gradle/
│   └── libs.versions.toml           # Version catalog
├── build.gradle.kts                 # Root build file
└── settings.gradle.kts
```

## Data Flow

```
Compose Screen ─── onIntent ───▶  ViewModel (MviViewModel<State, Intent, Effect>)
       ▲                              │
       │                              ├──▶ Repository (interface in domain/, impl in data/)
       │                              │       ├──▶ Ktor API service (remote)
       │                              │       ├──▶ Room DAO (local cache, LocalDate-typed)
       │                              │       └──▶ Mapper (DTO ↔ Domain, locale-aware)
       │                              │
       ├── state: StateFlow<UiState> ◀┤
       └── effects: Flow<UiEvent>    ◀┘  (one-shot, surfaced via CollectEffects)
```

Each ViewModel is built around three things:
- a `data class XUiState` — read by the screen via `vm.state.collectAsState()`
- a `sealed interface XIntent` — every user action is one case
- `effects: Flow<UiEvent>` — transient errors only

VMs are instantiated inside `App.kt`'s `entryProvider` blocks (via Koin), then `state` / `effects` / `onIntent` are passed to the single screen composable.

## Networking Pattern

```kotlin
// DTO — matches server JSON field names exactly
@Serializable
data class PollenDto(
    val id: Int,
    val desc: String,          // Russian name
    @SerialName("desc_eng")
    val descEng: String,       // English name
    // ...
)

// Domain model — clean, UI-ready
data class Pollen(
    val id: Int,
    val name: String,          // Locale-resolved name
    val description: String,   // Locale-resolved description
    val maxLevel: Int,
    val levels: List<PollenLevel>,
)

// Mapper
fun PollenDto.toDomain(locale: AppLocale): Pollen = Pollen(
    id = id,
    name = if (locale == AppLocale.RU) desc else descEng,
    // ...
)
```

## Locale Strategy

- `AppLocale` enum: `RU("ru")`, `EN("en")` — `tag: String` and `fromTag(s)` round-trip between enum and DataStore.
- Stored in DataStore preferences as a string tag; `LocaleProviderImpl` exposes it as `Flow<AppLocale>`.
- `applyMokoLocale(locale)` is called once from `KoinInit` after Koin starts. This sets `StringDesc.localeType` so moko-resources resolves `MR.strings.*` in the user's chosen language.
- **The UI does NOT receive `AppLocale`.** Compose reads strings via `stringResource(MR.strings.foo)` or `StringDesc.localized()`. No `LocaleProvider` injection in composables.
- **Data-layer mappers DO receive `AppLocale`** when picking between `_rus` and `_eng` fields from API responses. VMs that fetch such data inject `LocaleProvider`.
- Never hardcode `"ru"` / `"en"` outside `AppLocale` itself.

## Map Architecture (expect/actual)

```kotlin
// commonMain
@Composable
expect fun PlatformMapView(
    pins: List<MapPin>,
    polygons: List<MapPolygon>,
    onPinClick: (MapPin) -> Unit,
    modifier: Modifier,
)

// androidMain — Google Maps via maps-compose
@Composable
actual fun PlatformMapView(...) { /* GoogleMap composable */
}

// iosMain — stub for now
@Composable
actual fun PlatformMapView(...) { /* Text("Map not implemented") */
}
```

## Room Database

Database name: `"pollen_db"`, current version: **5**.

`LocalDateConverter` (`@TypeConverter`) is registered on the database so every `date: LocalDate` field on every entity stores as TEXT (`yyyy-MM-dd`) and reads back as `LocalDate`. Do not bridge `LocalDate ↔ String` in mappers, repos, or DAOs — the converter does it once at the persistence boundary.

Entities in `PollenDatabase` (match exactly — do not add or assume others exist):

- `UserEntity` — local user profile + server_id
- `SyncStateEntity` — last sync IDs (replaces `update_info`)
- `HealthEntryEntity` — health diary entries
- `PollenEntity` + `PollenLevelInfoEntity` — pollen type catalog (note: `PollenLevelInfoEntity`, not
  `PollenLevelEntity`)
- `LocationEntity` — monitoring stations
- `LevelEntity` — live pollen level measurements
- `ForecastLevelEntity` — forecast pollen data (separate entity from `LevelEntity`)
- `StatisticsEntity` — aggregated health stats
- `FriendEntity` — friends list
- `TherapyEntity` — medication tracking
- `PhenologyEntity` — phenology observations
- `DayActivityEntity` — daily engagement tracking

**Not cached in Room** (API-only, no local persistence):

- Feed data (expert comments, VK posts, media) — `FeedRepository` calls the API directly every time
- Map pins and polygon forecasts — fetched fresh each time
- Weather — fetched fresh from Open-Meteo each time

When adding a new entity, bump the database version and provide a `Migration`.

## Error Handling

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : ApiResult<Nothing>()
}

// Wrapper for Ktor calls — use at repository layer
suspend fun <T> safeApiCall(
    logger: Logger = Logger.withTag("safeApiCall"),
    description: String = "api call",
    block: suspend () -> T,
): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: CancellationException) {
    throw e  // never swallow coroutine cancellation
} catch (e: Exception) {
    logger.e(e) { "Failed to $description" }
    ApiResult.Error(e.message ?: "Unknown error", e)
}
```

ViewModels do their own `try/catch` around suspending repository calls and always:

1. Update state with `updateState { copy(field = LoadState.Failed) }`
2. Emit `emitEffect(UiEvent.ShowError(MR.strings.error_…desc()))` — message must be a `StringDesc` from `MR.strings.*`, not a raw string
3. Rethrow `CancellationException` before any general `catch (e: Exception)` block

For reactive `observe*()` flows from repos, defensive `.catch` is **only** needed when there's a real error source (rare). Cold DB/StateFlow-backed flows don't throw.

## Koin Module Organization

```
sharedModule        — repositories, locale provider (defined in shared commonMain)
networkModule       — Ktor HttpClient, API services
databaseModule      — Room database, DAOs, DataStore
viewModelModule     — all ViewModels
platformModule      — expect/actual platform bindings (defined per platform in androidMain/iosMain)
```

## MVI Pattern

Every ViewModel extends `MviViewModel<State, Intent, Effect>`. The base class lives at `shared/.../presentation/common/MviViewModel.kt` and provides exactly:

- `val state: StateFlow<State>` (private `MutableStateFlow` behind it)
- `val effects: Flow<Effect>` (private `Channel<Effect>` behind it)
- `currentState: State` getter for reads from inside the VM
- `onIntent(intent)` — entry point, dispatches to abstract `handleIntent(intent)`
- `updateState { copy(…) }` — reducer
- `emitEffect(effect)` — one-shot, snackbar etc.

**Do not grow this base class.** If you find yourself wanting a helper, write it inside the concrete VM instead — keeping the base API small means new VMs are obvious to read.

```kotlin
@Stable
data class FooUiState(
    val items: LoadState<ImmutableList<Item>> = LoadState.Loading,
    val query: String = "",
    val isSheetExpanded: Boolean = false,
)

sealed interface FooIntent {
    data object LoadData : FooIntent
    data class SearchQueryChanged(val query: String) : FooIntent
    data object ToggleSheet : FooIntent
}

class FooViewModel(
    private val repo: FooRepository,
) : MviViewModel<FooUiState, FooIntent, UiEvent>(FooUiState()) {

    init { observeItems() }

    override fun handleIntent(intent: FooIntent) {
        when (intent) {
            FooIntent.LoadData -> reload()
            is FooIntent.SearchQueryChanged -> updateState { copy(query = intent.query) }
            FooIntent.ToggleSheet -> updateState { copy(isSheetExpanded = !isSheetExpanded) }
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

### State slices — independent observers

When state has multiple async fields with **different** input dependencies, split them into independent slice observers — one private `observe<Slice>()` function per slice, each with its own `viewModelScope.launch { source.collect { updateState { copy(…) } } }`.

- **Only `combine` what truly shares inputs.** `userAllergens` needs pollens + sensitivities + levels — keep combined. `otherAllergens` only needs pollens + sensitivities — separate slice.
- **No final assembly combine.** Each slice updates its own state field; Compose recomposes on each `updateState`.
- **Intent-only state** (query, dialog visibility, sheet expanded) → just write directly from `handleIntent` via `updateState`. No slice observer, no `MutableStateFlow` field needed.
- **Internal source-of-truth flows** (e.g. `_selectedDate` that drives `flatMapLatest` chains) stay as private `MutableStateFlow` properties.

See `HomeViewModel` (10+ slices), `DiaryViewModel` (7 slices), `PhenologyViewModel` (3 slices) for concrete examples.

### When state has nested sub-objects

If `state.screenData: LoadState<ScreenDataUi>` wraps several sub-fields and the slices independently fill them, write a private `updateScreenData` helper inside the VM (NOT on the base class):

```kotlin
private inline fun updateScreenData(crossinline transform: ScreenDataUi.() -> ScreenDataUi) {
    updateState {
        val current = (screenData.dataOrNull) ?: EmptyScreenData
        copy(screenData = LoadState.Loaded(current.transform()))
    }
}
```

## Screen Pattern

A navigation-facing composable is a **single function** that takes `state` / `effects` / `onIntent` / navigation lambdas. No wrapper that injects the VM.

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

- `effects` and `onIntent` carry defaults so previews work with just `state`.
- For screens with 3+ intent-mapped callbacks, pass `onIntent` (not many lambdas). Navigation lambdas stay separate.
- Local UI state (dialog booleans, `LaunchedEffect`) lives **inside** this function. Don't add another wrapper.

## Navigation Pattern

VM creation happens in `App.kt`'s `entryProvider` block. Each `entry<XRoute>` instantiates its VM via Koin and passes slices to the screen:

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

Routes with parameters use `parametersOf`:
```kotlin
entry<ForecastDetailRoute> { route ->
    val vm = koinViewModel<ForecastDetailViewModel>(
        key = "forecast_${route.pollenId}",
    ) { parametersOf(route.pollenId) }
    val state by vm.state.collectAsState()
    // …
}
```

For routes hosting multiple VMs (e.g. `FeedRoute` embeds a friends-list tab), create both in the entry block and pass each VM's state/onIntent down — the embedded `FriendsListContent` is itself a stateless composable taking `state` + `onIntent`.

## Common UI Components

All in `shared/.../presentation/common/` — **use these, don't create new ones**:

| Component                                              | Purpose                                                          |
|--------------------------------------------------------|------------------------------------------------------------------|
| `MviViewModel<State, Intent, Effect>(initial)`         | Base for every ViewModel — owns state/effects                    |
| `CollectEffects(effects, snackbarHostState, onRetry?)` | Wires `Flow<UiEvent>` to Snackbar (called inside screens)        |
| `UiEvent.ShowError(message: StringDesc)`               | The only effect type — transient errors                          |
| `LoadState<T>`                                         | `Loading` / `Loaded<T>` / `Failed` sealed interface              |
| `FullScreenError(onRetry)`                             | Full-screen error with retry — use when all critical data failed |
| `ErrorBanner(onRetry)`                                 | Inline card-style warning — use when only some data failed       |
| `rememberCopyToClipboard()`                            | expect/actual `(String) -> Unit` clipboard write                 |
| `rememberShareTextLauncher()`                          | expect/actual `(String) -> Unit` share sheet                     |
| `formatDateLocalized(date: LocalDate)`                 | moko-resources backed date formatting (UI only)                  |
| `ShimmerEffect`                                        | Modifier extension for skeleton shimmer                          |
| `*Skeleton` composables                                | Per-section loading placeholders — grep `presentation/common/` for the one you need |
