# AndroidManifest & Permissions

## Permissions

| Permission | Purpose |
|---|---|
| `android.permission.INTERNET` | Network access |
| `android.permission.ACCESS_NETWORK_STATE` | Check connectivity |
| `android.permission.VIBRATE` | Haptic feedback |
| `android.permission.ACCESS_COARSE_LOCATION` | Approximate location |
| `android.permission.ACCESS_FINE_LOCATION` | Precise GPS location |
| `com.google.android.providers.gsf.permission.READ_GSERVICES` | Google services |
| `android.permission.WRITE_EXTERNAL_STORAGE` (maxSdkVersion=32) | Legacy file storage |
| `com.google.android.gms.permission.AD_ID` | Advertising ID for ad targeting |
| `android.permission.ACCESS_WIFI_STATE` | WiFi state |
| `android.permission.WAKE_LOCK` | Keep CPU awake (analytics/transport) |
| `com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE` | Install referrer tracking |
| `android.permission.ACCESS_ADSERVICES_ATTRIBUTION` | Android Privacy Sandbox |
| `android.permission.ACCESS_ADSERVICES_AD_ID` | Android Privacy Sandbox |
| Custom: `pollen.sgolovanov.pollen2.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | Internal receivers |

**uses-feature:** OpenGL ES 2.0 (required by Google Maps)
**queries:** `com.google.android.apps.maps`

---

## Activities (12 app activities + 2 SDK activities)

### App Activities

| Activity | Exported | Role |
|---|---|---|
| `SplashActivity` | YES (LAUNCHER) | Entry point. 1-second delay, routes to StartActivity or MainActivity |
| `MainActivity` | YES | Core activity. Map, location, ads, fragments, API calls. ~2000+ lines |
| `StartActivity` | YES | EULA acceptance. Loads `https://pollen.club/start/` in WebView |
| `DonateActivity` | NO | Donation page (`https://pollen.club/offer/`) |
| `CureActivity` | NO | Medication management |
| `HelpActivity` | NO | Help screens (9 fragment pages) |
| `HelpActivity2` | NO | Additional help screens |
| `SearchCureActivity` | NO | Medication search |
| `FriendsActivity` | NO | Friends management |
| `LanguagesActivity` | NO | Language selection |
| `AllergensActivity` | NO | Allergen viewing |
| `LocationsActivity` | NO | Location management |
| `SelectAllergensActivity` | NO | Allergen selection |
| `UserPropertiesActivity` | NO | User profile editing |

### SDK Activities

| Activity | SDK |
|---|---|
| `com.yandex.mobile.ads.common.AdActivity` | Yandex Ads |
| `com.yandex.mobile.ads.features.debugpanel.ui.IntegrationInspectorActivity` | Yandex Ads debug |
| `com.google.android.gms.common.api.GoogleApiActivity` | Google Play Services |

---

## Services

| Service | Exported | SDK |
|---|---|---|
| `ComponentDiscoveryService` | NO | Firebase (discovers Analytics, Crashlytics, etc.) |
| `AppMeasurementService` | NO | Google Analytics |
| `AppMeasurementJobService` | NO | Google Analytics |
| `AppMetricaService` | NO | Yandex AppMetrica |
| `TransportBackendDiscovery` | NO | Google Data Transport |
| `JobInfoSchedulerService` | NO | Google Data Transport |

---

## Receivers

| Receiver | Exported | Purpose |
|---|---|---|
| `AppMeasurementReceiver` | NO | Google Analytics |
| `ProfileInstallReceiver` | YES | AndroidX profile installer |
| `AlarmManagerSchedulerBroadcastReceiver` | NO | Google Data Transport |

---

## Content Providers

| Provider | Authority | Exported | Purpose |
|---|---|---|---|
| `MobileAdsInitializeProvider` | `*.MobileAdsInitializeProvider` | NO | Yandex Ads auto-init |
| `DebugPanelFileProvider` | `*.monetization.ads.inspector.fileprovider` | NO | Yandex Ads debug |
| `InitializationProvider` | `*.androidx-startup` | NO | AndroidX Startup |
| `FirebaseInitProvider` | `*.firebaseinitprovider` | NO | Firebase auto-init |
| `PreloadInfoContentProvider` | `*.appmetrica.preloadinfo.retail` | **YES** | AppMetrica preload attribution |

---

## Deep Links / URL Schemes

**None.** No `BROWSABLE` intent filters, no custom URL schemes registered.

The only scheme is internal `appmetrica` for AppMetrica IPC.

---

## External URLs Opened by App

| URL | How | Where |
|---|---|---|
| `https://pollen.club/start/` | WebView | StartActivity |
| `https://pollen.club` | Browser intent | MainActivity |
| `https://pollen.club/guide/` | Browser intent | MyMapFragment |
| `https://pollen.club/offer/` | Browser intent | DonateActivity |
| `https://pollen.club/maps/map.html` | Dynamic (stored in DB) | update_info default |
| `https://vk.com/club87598739` | Browser intent | MainActivity |
| `https://www.facebook.com/groups/847068988689032` | Browser intent | MainActivity |

---

## App Navigation Flow

```
SplashActivity (1s delay)
    ├── if NOT accepted terms → StartActivity (WebView EULA)
    │                              └── Accept → MainActivity
    └── if accepted → MainActivity
                        ├── CentralFragment (home/dashboard)
                        ├── MyMapFragment (pollen map)
                        ├── SymptomsFragment (health diary)
                        ├── CommentsFragment (expert feed)
                        ├── PhenologiesFragment (observations)
                        └── UserMenuView (side menu)
                              ├── UserPropertiesActivity
                              ├── AllergensActivity / SelectAllergensActivity
                              ├── LocationsActivity
                              ├── LanguagesActivity
                              ├── FriendsActivity
                              ├── HelpActivity / HelpActivity2
                              └── DonateActivity
```

---

## Push Notifications

**NOT configured.** No FCM, no `FirebaseMessagingService`, no push token handling anywhere in the app.
