# App Package Structure

All packages under `pollen.sgolovanov`:

---

## `pollen.sgolovanov.pollen2` — Core/Main

| Class | Role |
|---|---|
| `PollenApplication` | Application class. Locale setup, Google Maps bug fix |
| `SplashActivity` | 1-second splash screen. Routes based on EULA acceptance |
| `StartActivity` | Terms acceptance. Loads `https://pollen.club/start/` in WebView |
| `MainActivity` | **God Activity** (~2000+ lines). Contains: map, location tracking, ad management, user data, fragment navigation, API calls, Firebase analytics. Implements `OnMapReadyCallback` + `LocationListener`. Has static `INSTANCE` field used as service locator |
| `DonateActivity` | Donation page |
| `HelpActivity` | Help/tutorial (9 fragment pages) |
| `HelpActivity2` | Additional help screens |
| `GraphicView` | Custom chart/graph view |
| `GraphicPointValue` | Data point for charts |
| `CircleImageView` | Circular image view |
| `JustifyTextView` | Justified text view |
| `MaxHeightScrollView` | ScrollView with max height constraint |
| `SelectView` | Custom selection view |
| `SmileView` | Emoji/mood display view |
| `HandbookItemView` | Handbook list item view |
| `PropertyItemView` | Property/setting item view |

---

## `pollen.sgolovanov.base` — Base Layer

| Class | Role |
|---|---|
| `BaseActivity` | Abstract activity. Locale handling, toolbar, permissions, fragment management, progress dialog, error handling, RxJava disposable management |
| `BaseFragment` | Base fragment with `BaseMvpView` |
| `BaseDialogFragment` | Base dialog fragment with `BaseMvpView` |
| `BaseMvpView` | Interface: `onError(message)`, `onNetworkError()`, `onAccountError()`, `isNetworkConnected()` |
| `Constants` | Analytics event name constants |
| `OnPermissionListener` | Permission callback interface |

---

## `pollen.sgolovanov.home` — Home/Dashboard

| Class | Role |
|---|---|
| `CentralFragment` | Main dashboard. Shows pollen levels for selected location |
| `PollensListAdapter` | RecyclerView adapter for pollen level list |
| `PollenItemView` | Individual pollen level item view |
| `PollenValues` | Pollen value data holder |

---

## `pollen.sgolovanov.map` — Map Feature

| Class | Role |
|---|---|
| `MyMapFragment` | Google Maps fragment. Polygon overlays for allergen forecasts, user pins, friend pins |
| `myClusterItem` | Marker clustering item implementing `ClusterItem` |
| `HashTagView` | Hashtag display overlay on map |

---

## `pollen.sgolovanov.symptoms` — Health/Symptoms Tracking

| Class | Role |
|---|---|
| `SymptomsFragment` | Symptoms input/display |
| `CureActivity` | Medication management activity |
| `SearchCureActivity` | Medication search activity |
| `CalendarView` | Custom calendar component |
| `CalendarOneLine` | Single-line calendar |
| `DayRect` | Day rectangle in calendar |
| `CuresAdapter` | Medications list adapter |
| `CureTypesAdapter` | Medication types adapter |
| `SearchAdapter` | Search results adapter |
| `CureItemView` | Medication item view |
| `SelectCureItemView` | Selectable cure item |
| `SelectCureView` | Cure selection view |
| `CureScheme` | Medication dosage scheme data |

---

## `pollen.sgolovanov.comments` — Social/Comments Feed

| Class | Role |
|---|---|
| `CommentsFragment` | Comments/feed display fragment |
| `CommentsAdapter` | Expert comments RecyclerView adapter |
| `VkAdapter` | VK social feed adapter (interleaves native ads every 3 items) |
| `MediaAdapter` | Media content adapter |
| `FriendsAdapter` | Friends in comments adapter |
| `FixedSizeVideoView` | Custom ExoPlayer video view for inline video playback |

---

## `pollen.sgolovanov.fenologies` — Phenology Observations

| Class | Role |
|---|---|
| `PhenologiesFragment` | Phenology observation recording |
| `PhenologyAdapter` | Observation list adapter |
| `PhenologyHolder` | ViewHolder for phenology items |
| `PhenologyItemView` | Observation item view |
| `SelectPhenologyView` | Phenology state selection view |

---

## `pollen.sgolovanov.user_menu` — Settings/Profile

| Class | Role |
|---|---|
| `UserMenuView` | Side menu/settings navigation panel |
| `AllergensActivity` | View allergen info |
| `SelectAllergensActivity` | Select user's allergens |
| `LocationsActivity` | Manage monitoring locations |
| `LanguagesActivity` | Language switching |
| `FriendsActivity` | Friends management |
| `UserPropertiesActivity` | User profile editing |
| `FriendsAdapter` | Friends list adapter |
| `LocationsAdapter` | Locations list adapter |
| `SelectAllergensAdapter` | Allergen selection adapter |

---

## `pollen.sgolovanov.database` — Local Database

| Class | Role |
|---|---|
| `PollenSQLiteHelper` | SQLiteOpenHelper subclass. DB name "Pollen", version 9 |
| `users` | Users table model/helper |
| `update_info` | Sync state table model/helper |
| `comments` | Comments table model |
| `health` | Health diary table model |
| `summary_health` | Health statistics table model |
| `locations` | Locations table model |
| `weather_prognosis` | Weather data table model |
| `fenologies` | Phenology table model |
| `day_activities` | Daily activity tracking model |
| `pollens` | Pollen types table model |
| `levels_info` | Level definitions table model |
| `levels` | Pollen measurements table model |
| `post_message` | Message queue table model |
| `therapies` | Therapy tracking table model |
| `Friends` | Friends table model |
| `lenta_vk` | VK feed table model |

---

## `pollen.sgolovanov.restapi` — Networking

| Class | Role |
|---|---|
| `RestClient` | Kotlin object (singleton). Creates OkHttpClient + 2 Retrofit instances |
| `ApiService` | Retrofit interface: 21 endpoints to `data.pollen.club/api_2/` |
| `ApiOutsideService` | Retrofit interface: 2 external endpoints (api.pollen.club, forecast.io) |
| `RequestInterceptor` | OkHttp network interceptor — **NO-OP** (does nothing) |
| `LongTypeAdapter` | Gson adapter: safely handles null/string Long values (returns 0) |

---

## `pollen.sgolovanov.parsers` — API Data Models (~35 classes)

All request body classes and response data classes for API communication. See `03-data-models.md` for complete field-by-field definitions.

---

## `pollen.sgolovanov.utils` — Utilities

| Class | Role |
|---|---|
| `KeyboardUtils` | Keyboard show/hide helpers |
| `NumberExtKt` | Kotlin number extension functions |
| `StringExtKt` | Kotlin string extension functions |
| `TimeUtils` | Date/time formatting utilities |
