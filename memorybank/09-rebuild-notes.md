# Rebuild Notes & Recommendations

## Things to Replace

### 1. Dark Sky Weather API (DEAD)
The original app uses `https://api.forecast.io/forecast/{key}/{lat},{lon}?units=si` which was sunset by Apple in March 2023. Alternatives:
- **Open-Meteo** — free, no API key needed, good EU coverage
- **OpenWeatherMap** — free tier available, popular
- **Weather API** — free tier available

The app only uses `currently.summary`, `currently.icon`, and `currently.temperature` from the response.

### 2. Raw SQLite → Room
The original uses `PollenSQLiteHelper` with raw SQL queries and manual cursor reads across 16 tables. Room provides:
- Compile-time SQL verification
- LiveData/Flow integration
- Migration support
- Type converters

### 3. God Activity → Proper Architecture
`MainActivity` is ~2000+ lines handling everything. Recommended:
- MVVM or MVI architecture
- ViewModels per feature
- Repository pattern for data access
- Hilt/Koin for dependency injection

### 4. Allergen Booleans → Flexible Structure
The `users` table stores allergens as individual boolean columns (`is_bereza`, `is_dub`, etc.). This is rigid. Better:
- Separate `user_allergens` table with FK to allergens
- Or a JSON array/set in user preferences

### 5. No Error Handling → Proper Error Strategy
Add:
- Retrofit `CallAdapter` or `Result` wrapping
- Global error handler via `CoroutineExceptionHandler`
- Retry with exponential backoff for transient failures
- Proper connectivity checking (not just `getActivity() != null`)

### 6. Bilingual Hardcoding → Proper i18n
All models carry `_rus`/`_eng` field pairs. Consider:
- Android resource system (`strings.xml`) for static text
- Server sending locale-appropriate responses based on `Accept-Language` header
- Or continue with the dual-field approach but centralize the selection logic

---

## Things to Keep

### 1. Incremental Sync Pattern
The `from_id` pattern for `get_levels`, `get_forecasts`, `get_statistics` is efficient. Keep this approach — it minimizes data transfer.

### 2. API Structure
The server API at `data.pollen.club/api_2/` appears stable. The 21 endpoints are well-defined. The server expects:
- POST bodies as JSON with Content-Type: application/json
- GET with query parameters
- No authentication headers

### 3. User Identity Model
Simple numeric `user_id` assigned by server. No login/password. This is by design for the app — users don't create accounts with credentials.

### 4. Allergen Forecast Polygons
`api.pollen.club/static/forecasts/{allergen}/{dateTime}.json` provides pre-computed polygon data for map overlays. This is well-structured and should continue to work.

---

## Key Pollen Types (from users table columns)

| Russian | Transliteration | English |
|---|---|---|
| Берёза | Bereza | Birch |
| Дуб | Dub | Oak |
| Полынь | Polyn | Wormwood/Mugwort |
| Ольха | Olkha | Alder |
| Орешник | Oreshnik | Hazel |
| Злаки | Zlaki | Grasses |
| Маревые | Marevye | Goosefoot/Chenopod |
| Амброзия | Ambrosia | Ragweed |

---

## Symptom Categories

| Field | Body Area |
|---|---|
| `eyes` | Eye symptoms |
| `nose` | Nasal symptoms |
| `throat` | Throat symptoms |
| `lunds` | Lung symptoms (typo for "lungs" in original) |
| `general` | General wellbeing |
| `other` | Free text for other symptoms |

---

## Feeling Scale

Used in `add_user_feel.php` and map pins:
- Values appear to be integers (0, 1, 2)
- Likely: 0 = good, 1 = middle, 2 = bad
- Statistics aggregate these as `good`, `middle`, `bad` counts

---

## Ad Integration Notes

- Server controls ad visibility via `check_ads_for_user.php`
- `show_banners` boolean gates all ad display
- `show_counter` (default 5) controls interstitial frequency
- Server-side banners (`get_screen_banners.php`) have date ranges, daily impression limits, and duration
- Native ads are mixed into VK feed content (every 3 items)
- Yandex Mobile Ads is the sole ad provider

---

## Data Sync Flow (App Startup)

Approximate startup sequence from `MainActivity`:

1. `setUser()` — register/update user, get `user_id`
2. `checkAndroidVersion()` — check for app updates
3. `checkAdsForUser()` — determine ad settings
4. `getScreenBanners()` — load promotional banners
5. `getPollens()` — load pollen type catalog
6. `getLocations()` — load monitoring stations (via `CentralFragment`)
7. `getLevels(from_id)` — incremental pollen level data
8. `getForecastData(from_id)` — incremental forecast data
9. `getComments()` — expert comments + VK feed
10. `getHashTags()` — map hashtag data
11. `getUserForecast()` — personalized forecast
12. `getStatistics(from_id)` — health statistics
