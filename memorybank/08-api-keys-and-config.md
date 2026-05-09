# API Keys, Credentials, and Configuration Constants

## API Keys

| Key | Value | Service | Status |
|---|---|---|---|
| Google Maps API Key | `AIzaSyCnVnnhlMYqql9esCIsFGjMVwiJwUj38ic` | Google Maps SDK | Active |
| Dark Sky API Key | `9a91a6bdaf34c63238d2e6f7813b77bb` | forecast.io weather | **DEAD** (API sunset March 2023) |
| Google/Firebase API Key | `AIzaSyAXk57b-IQ9lZxVucJKZqCOjPvBdp-CuHg` | Firebase services | Active |

## Yandex Ads Block IDs

| Block ID | Ad Format |
|---|---|
| `R-M-6137939-2` | Native Bulk Ads |
| `R-M-6137939-3` | Interstitial Ads |
| `R-M-6137939-5` | App Open Ads |

## Firebase / Google Services Configuration

| Key | Value |
|---|---|
| `google_app_id` | `1:825956231925:android:3e4c45b2206ea1adcef3bc` |
| `gcm_defaultSenderId` | `825956231925` |
| `google_api_key` | `AIzaSyAXk57b-IQ9lZxVucJKZqCOjPvBdp-CuHg` |
| `google_crash_reporting_api_key` | `AIzaSyAXk57b-IQ9lZxVucJKZqCOjPvBdp-CuHg` |
| `google_storage_bucket` | `pollenclubandroid.appspot.com` |
| `project_id` | `pollenclubandroid` |
| `default_web_client_id` | `825956231925-v2qjc7ufo85drpkn2u56ausih6igit49.apps.googleusercontent.com` |

## Build Constants

From `BuildConfig.java`:

```java
public static final String APPLICATION_ID = "pollen.sgolovanov.pollen2";
public static final int VERSION_CODE = 68;
public static final String VERSION_NAME = "6.3.6";
public static final String BUILD_TYPE = "release";
public static final boolean DEBUG = false;
public static final String API_URL = "https://data.pollen.club/api_2/";
```

## Social Media Links

| Platform | URL |
|---|---|
| VKontakte | `https://vk.com/club87598739` |
| Facebook | `https://www.facebook.com/groups/847068988689032` |

## Website URLs

| URL | Purpose |
|---|---|
| `https://pollen.club` | Main website / news |
| `https://pollen.club/start/` | Terms/EULA (WebView) |
| `https://pollen.club/guide/` | Map guide |
| `https://pollen.club/offer/` | Donation page |
| `https://pollen.club/maps/map.html` | Default map URL (dynamic, server can update) |

## Network Config

| Setting | Value |
|---|---|
| Cleartext traffic | Globally permitted |
| Trust anchors | System certificates only |
| SSL pinning | None |
| HTTPS enforcement | None |
