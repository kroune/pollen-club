# Third-Party SDKs and Services

## Advertising

### Yandex Mobile Ads SDK
- Package: `com.yandex.mobile.ads`
- Primary ad network for all ad formats
- Auto-initialized via `MobileAdsInitializeProvider` content provider

**Ad Unit IDs:**

| ID | Format | Class |
|---|---|---|
| `R-M-6137939-2` | Native Bulk Ads | `NativeBulkAdLoader` (loads 5 at a time) |
| `R-M-6137939-3` | Interstitial Ads | `InterstitialAdLoader` |
| `R-M-6137939-5` | App Open Ads | `AppOpenAdLoader` |

**Ad rendering:** Native ads are interleaved in the VK feed (every 3 items) via `VkAdapter`. Interstitials shown after every N screens (controlled by `show_counter` from server, default 5). App open ads shown on cold start.

### Yandex DivKit
- Package: `com.yandex.div`, `com.yandex.div2`, `com.yandex.divkit`
- Server-driven UI rendering for ad creatives

---

## Analytics

### Firebase Analytics
- Package: `com.google.firebase.analytics`
- Events tracked in `MainActivity`:
  - `SHOW_ADS` (param: `START`, `INTERSTITIAL`)
  - `CLICK_TO_ADS`
  - `CLICK_TO_BANNER`
  - `SHOW_BANNER`

### Firebase Crashlytics
- Package: `com.google.firebase.crashlytics`
- Crash reporting, auto-initialized via `ComponentDiscoveryService`
- Mapping file ID: `00000000000000000000000000000000` (no ProGuard mapping)

### Yandex AppMetrica
- Package: `io.appmetrica.analytics`
- Russian analytics SDK
- Runs background service: `AppMetricaService`
- Has exported content provider for OEM preload attribution

### Yandex Varioqub
- Package: `com.yandex.varioqub`
- A/B testing / feature flags from Yandex

---

## Maps & Location

### Google Maps SDK
- Package: `com.google.android.gms.maps`
- Used in `MyMapFragment` with `GoogleMap`, `OnMapReadyCallback`
- **API Key:** `AIzaSyCnVnnhlMYqql9esCIsFGjMVwiJwUj38ic`

### Google Maps Utils
- Package: `com.google.maps`
- Marker clustering via `ClusterManager` and `myClusterItem`

### Google Play Services Location
- Package: `com.google.android.gms`
- Location services, core services

---

## Networking

### Retrofit 2
- Package: `retrofit2`
- REST API client

### OkHttp 3
- Package: `okhttp3`
- HTTP client

### Gson
- Package: `com.google.gson`
- JSON serialization/deserialization

### RxJava 2
- Package: `io.reactivex`
- Reactive programming (call adapter registered but actual calls use coroutines)

### RxAndroid
- Package: `io.reactivex.android`
- Android scheduler for RxJava

### RxBinding 2
- Package: `com.jakewharton.rxbinding2`
- Reactive view bindings

---

## Media

### Glide
- Package: `com.bumptech.glide`
- Image loading for banners, media content, profile images

### ExoPlayer
- Package: `com.google.android.exoplayer2`
- Video playback in comments section via `FixedSizeVideoView`

### AndroidSVG
- Package: `com.caverock.androidsvg`
- SVG rendering

---

## UI Libraries

### LiquidSwipe
- Package: `com.madapps.liquid`
- Liquid swipe animations

### MKLoader
- Package: `com.tuyenmonkey.mkloader`
- Loading spinner animations

---

## Localization

### LocaleHelper
- Package: `com.zeugmasolutions.localehelper`
- In-app language switching

---

## Debug

### Curl Logger
- Package: `com.grapesnberries.curllogger`
- HTTP request logging (debug builds)

---

## DI

### Yatagan
- Package: `com.yandex.yatagan`
- Yandex's lightweight dependency injection framework (Dagger-like)

---

## AndroidX Libraries

- AppCompat
- ConstraintLayout
- ViewPager2
- RecyclerView
- SwipeRefreshLayout
- Fragment
- Lifecycle
- DataStore
- ViewBinding
- Emoji2
- Startup (InitializationProvider)
- ProfileInstaller

---

## Firebase Configuration

| Key | Value |
|---|---|
| `google_app_id` | `1:825956231925:android:3e4c45b2206ea1adcef3bc` |
| `gcm_defaultSenderId` | `825956231925` |
| `google_api_key` | `AIzaSyAXk57b-IQ9lZxVucJKZqCOjPvBdp-CuHg` |
| `google_crash_reporting_api_key` | `AIzaSyAXk57b-IQ9lZxVucJKZqCOjPvBdp-CuHg` |
| `google_storage_bucket` | `pollenclubandroid.appspot.com` |
| `project_id` | `pollenclubandroid` |
| `default_web_client_id` | `825956231925-v2qjc7ufo85drpkn2u56ausih6igit49.apps.googleusercontent.com` |

**Firebase modules in use:** Analytics, Crashlytics, Installations, Sessions, Data Transport

**No FCM (push notifications)** — `gcm_defaultSenderId` exists only because `google-services.json` plugin generates it by default.
