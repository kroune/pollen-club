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
Compose Screen
    → ViewModel (StateFlow<UiState>)
        → Repository (interface in domain/, impl in data/)
            → Ktor API service (remote data)
            → Room DAO (local cache)
            → Mapper (DTO ↔ Domain)
        ← Flow<DomainModel>
    ← UiState rendered by Compose
```

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

- `AppLocale` enum: `RU`, `EN`
- Stored in DataStore preferences
- User can change in Settings
- All mappers receive the current locale to pick `_rus`/`_eng` fields
- Locale is provided via Koin or a `LocaleProvider` interface so mappers stay testable

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

Database name: `"pollen_db"`, current version: **2**

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

ViewModels do their own `try/catch` around repository calls and always:

1. Set the relevant `LoadState.Failed` in state
2. Send a `UiEvent.ShowError("user-friendly message")` through the event channel
3. Rethrow `CancellationException` before any general `catch (e: Exception)` block

## Koin Module Organization

```
sharedModule        — repositories, locale provider (defined in shared commonMain)
networkModule       — Ktor HttpClient, API services
databaseModule      — Room database, DAOs, DataStore
viewModelModule     — all ViewModels
platformModule      — expect/actual platform bindings (defined per platform in androidMain/iosMain)
```

## ViewModel Pattern

Every ViewModel follows this exact shape. Do not invent alternatives.

```kotlin
data class FooUiState(
    val items: LoadState<ImmutableList<Item>> = LoadState.Loading,
    // other async fields also use LoadState<T>
)

class FooViewModel(
    private val repo: FooRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FooUiState())
    val state: StateFlow<FooUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        _state.value = FooUiState()  // reset to Loading
        viewModelScope.launch {
            try {
                val result = repo.getItems()
                _state.value = _state.value.copy(items = LoadState.Loaded(result.toImmutableList()))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(items = LoadState.Failed)
                _events.send(UiEvent.ShowError("Failed to load items"))
            }
        }
    }
}
```

For repositories that return `Flow`, use `stateIn`:

```kotlin
val items: StateFlow<ImmutableList<Item>> = repo.observeItems()
    .map { it.toImmutableList() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), persistentListOf())
```

## Common UI Components

All in `shared/.../presentation/common/` — **use these, don't create new ones**:

| Component                                            | Purpose                                                          |
|------------------------------------------------------|------------------------------------------------------------------|
| `CollectEvents(events, snackbarHostState, onRetry?)` | Wires `Channel<UiEvent>` to Snackbar                             |
| `FullScreenError(onRetry)`                           | Full-screen error with retry — use when all critical data failed |
| `ErrorBanner(onRetry)`                               | Inline card-style warning — use when only some data failed       |
| `ShimmerEffect`                                      | Modifier extension for skeleton shimmer                          |
| `LocationHeaderSkeleton`                             | Skeleton for location header                                     |
| `WeatherCardSkeleton`                                | Skeleton for weather card                                        |
| `PollenLevelCardSkeleton`                            | Skeleton for a single pollen card                                |
| `PollenListSkeleton`                                 | Skeleton for the full pollen list                                |
| `FeedCardSkeleton` / `FeedListSkeleton`              | Skeleton for feed/news cards                                     |
| `MedicationCardSkeleton` / `MedicationListSkeleton`  | Skeleton for medication rows                                     |
| `CategoriesCardSkeleton`                             | Skeleton for medication categories card                          |
| `FriendsListSkeleton`                                | Skeleton for friends list with avatar rows                       |
| `DayStripSkeleton`                                   | Skeleton for day forecast strip with navigation                  |
| `PersonalIndexCardSkeleton`                          | Skeleton for personal pollen index card                          |
| `MapChipRowSkeleton` / `MapAreaSkeleton`             | Skeleton for map filter chips and map area                       |
| `ForecastDetailHeaderSkeleton`                       | Skeleton for forecast detail header                              |
| `ForecastDetailChartSkeleton`                        | Skeleton for forecast detail chart                               |
| `ForecastDetailStatsSkeleton`                        | Skeleton for forecast detail stats card                          |
