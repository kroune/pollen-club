# API Endpoints — Complete Reference

## Base URL

```
https://data.pollen.club/api_2/
```

## Authentication

**There is NO authentication.** No OAuth, no bearer tokens, no API keys in headers, no cookies, no sessions.

The identity model:
1. On first launch, app calls `set_user.php` with `id=0`
2. Server responds with a `user_id` (long integer)
3. This `user_id` is stored in local SQLite (`users.server_id`)
4. All subsequent POST requests include this `user_id` in the JSON body
5. The OkHttp interceptor (`RequestInterceptor`) is a no-op — adds nothing

---

## POST Endpoints

### 1. `set_user.php` — Register / Update User

**Request:**
```json
{
  "id": 0,
  "name": "John",
  "last_name": "Doe",
  "location": 1,
  "ages": 30,
  "activity": 1
}
```

**Response (`SetUserResponse`):**
```json
{
  "result": true,
  "message": "OK",
  "user_id": 12345
}
```

**Notes:** `id` is the current `server_id` (0 on first call). Server returns the assigned `user_id`. This is the only endpoint that creates user identity.

---

### 2. `add_user_feel.php` — Submit How User Feels

**Request (`AddUserFeelRequest`):**
```json
{
  "date": "2024-04-15",
  "location": 1,
  "time": 1713180000,
  "opinion": 2,
  "opinion_old": 1,
  "latitude": 55.7558,
  "longitude": 37.6173,
  "user_id": 12345,
  "default_pollen": 3,
  "tags": "birch,alder",
  "location_name": "Moscow"
}
```

**Response (`AddUserFeelData`):**
```json
{
  "result": true,
  "message": "OK",
  "user_id": 12345,
  "statistics": {
    "id": 1,
    "date": "2024-04-15",
    "location": 1,
    "good": 45,
    "middle": 30,
    "bad": 25
  }
}
```

**Notes:** `opinion` values: likely 0=good, 1=middle, 2=bad. `tags` is a comma-separated string of allergen tags.

---

### 3. `add_user_cure_v2.php` — Submit Medication/Treatment

**Request (`AddUserFeelAndSymptomsRequest`):**
```json
{
  "user_id": 12345,
  "date": "2024-04-15",
  "cure_id": 10,
  "cure_name": "Cetirizine",
  "forma_id": 1,
  "forma": "Tablets",
  "frequency_id": 2,
  "frequency": "Twice daily",
  "use_from": "2024-04-01",
  "dose_id": 3,
  "dose": "10mg",
  "dose_value": 10,
  "frequency_value": 2,
  "type": 1
}
```

**Response (`AddUserFeelAndSymptomsData`):**
```json
{
  "result": true,
  "message": "OK",
  "user_id": 12345
}
```

---

### 4. `add_user_symptoms.php` — Submit Symptoms

**Request (`AddUserSymptomsRequest`):**
```json
{
  "user_id": 12345,
  "symptoms": [
    {
      "date": "2024-04-15",
      "tags": "birch",
      "nose": 2,
      "throat": 1,
      "eyes": 3,
      "general": 1,
      "lunds": 0,
      "other": "headache"
    }
  ]
}
```

**Response (`AddUserFeelAndSymptomsData`):**
```json
{
  "result": true,
  "message": "OK",
  "user_id": 12345
}
```

**Notes:** `symptoms` is an array — can submit multiple days at once. Severity values are integers (0=none, higher=worse). `lunds` = lungs (typo in original).

---

### 5. `add_fenology.php` — Submit Phenology Observation

**Request (`AddFenologyRequest`):**
```json
{
  "user_id": 12345,
  "date": "2024-04-15",
  "time": 1713180000,
  "comment": "Birch starting to flower",
  "state": 2,
  "latitude": 55.7558,
  "longitude": 37.6173
}
```

**Response (`BaseResponse`):**
```json
{
  "result": true,
  "message": "OK"
}
```

---

### 6. `add_friend.php` — Add Friend

**Request (`AddFriendRequest`):**
```json
{
  "user_id": 12345,
  "friend_id": 67890
}
```

**Response (`BaseResponse`):**
```json
{
  "result": true,
  "message": "OK"
}
```

**Notes:** Field in Java source is named `ussrId` but serialized as `"user_id"` via `@SerializedName`.

---

### 7. `delete_friend.php` — Delete Friend

**Request (`DeleteFriendRequest`):**
```json
{
  "user_id": 12345,
  "friend_id": 67890
}
```

**Response (`BaseResponse`):**
```json
{
  "result": true,
  "message": "OK"
}
```

---

### 8. `check_ads_for_user.php` — Check Ad Eligibility

**Request (`GetUserRequest`):**
```json
{
  "user_id": 12345
}
```

**Response (`CheckAdsData`):**
```json
{
  "result": true,
  "show_banners": true,
  "show_counter": 5
}
```

**Notes:** `show_counter` defaults to 5 — likely controls how many screens before showing interstitial.

---

### 9. `check_android_version.php` — Version Check

**Request (`CheckAndroidVersionRequest`):**
```json
{
  "build": 68
}
```

**Response (`CheckAndroidVersionData`):**
```json
{
  "is_old_version": false
}
```

---

### 10. `get_comments_and_lenta.php` — Expert Comments + Feed

**Request (`GetUserRequest`):**
```json
{
  "user_id": 12345
}
```

**Response (`CommentsData`):**
```json
{
  "comments": [
    {
      "id": 1,
      "date": "2024-04-15",
      "expert": 1,
      "comment": "Березовая пыльца достигла пика",
      "locations_id": 1,
      "comment_eng": "Birch pollen has peaked",
      "pin": 1
    }
  ],
  "lenta_vk": [
    {
      "id": 1,
      "date": "2024-04-15",
      "location": "Moscow",
      "information": "VK post text content",
      "pin": 0
    }
  ],
  "lenta_media": [
    {
      "id": 1,
      "date": "2024-04-15",
      "media_type": "image",
      "url": "https://example.com/image.jpg",
      "info_rus": "Описание",
      "info_eng": "Description",
      "pin": 0
    }
  ],
  "friends": [
    {
      "id": 1,
      "location": "Moscow",
      "date": "2024-04-15",
      "time": 1713180000,
      "friend_id": 67890,
      "opinion": 1
    }
  ]
}
```

**Notes:** `lenta_media` URLs are loaded via Glide. `media_type` can be image or video (ExoPlayer used for video).

---

### 11. `get_friends.php` — Get Friends List

**Request (`GetUserRequest`):**
```json
{
  "user_id": 12345
}
```

**Response (`FriendsData`):**
```json
{
  "friends": [
    { "id": 1, "friend_id": 67890 }
  ]
}
```

---

### 12. `get_pins_with_friends.php` — Get Map Pins

**Request (`GetUserRequest`):**
```json
{
  "user_id": 12345
}
```

**Response (`PinsData`):**
```json
{
  "pins": [
    {
      "date": "2024-04-15",
      "value": 2,
      "latitude": 55.7558,
      "longitude": 37.6173,
      "pollen_type": 3,
      "tags": "birch",
      "friend_id": 0
    }
  ]
}
```

**Notes:** `friend_id=0` means it's the user's own pin; non-zero means it's a friend's pin.

---

### 13. `get_statistics.php` — Get Health Statistics

**Request (`GetStatisticsRequest`):**
```json
{
  "from_id": 0
}
```

**Response (`StatisticsData`):**
```json
{
  "statistics": [
    {
      "id": 1,
      "date": "2024-04-15",
      "location": 1,
      "good": 45,
      "middle": 30,
      "bad": 25
    }
  ]
}
```

**Notes:** Uses incremental sync — `from_id` is the last known ID, server returns only newer records.

---

### 14. `get_forecasts.php` — Get Pollen Forecast Data

**Request (`LevelForecastRequest`):**
```json
{
  "from_id": 0
}
```

**Response (`LevelForecastData`):**
```json
{
  "levels": [
    {
      "id": 1,
      "date": "2024-04-15",
      "pollen": 3,
      "location": 1,
      "value": 4
    }
  ]
}
```

**Notes:** Uses incremental sync via `from_id`.

---

### 15. `get_user_forecast.php` — Get Personalized Forecast

**Request (`GetUserForecastsRequest`):**
```json
{
  "user_id": 12345
}
```

**Response (`UserForecastData`):**
```json
{
  "result": {
    "user_info": [
      { "id": "1", "desc_rus": "Прогноз", "desc_eng": "Forecast" }
    ],
    "user_forecast": [
      { "id": "1", "date": "2024-04-15", "value": "3" }
    ]
  }
}
```

---

## GET Endpoints

### 16. `get_levels.php?from_id={id}` — Get Pollen Levels

**Query parameter:** `from_id` (int) — last known record ID for incremental sync

**Response (`LevelData`):**
```json
{
  "result": [
    {
      "id": 100,
      "date": "2024-04-15",
      "pollen": 3,
      "location": 1,
      "value": 4
    }
  ]
}
```

---

### 17. `get_locations.php` — Get Monitoring Stations

**Response (`LocationData`):**
```json
{
  "locations": [
    {
      "id": 1,
      "desc": "Москва",
      "comment": "Центральный район",
      "latitude": 55.7558,
      "longitude": 37.6173,
      "eng_name": "Moscow",
      "eng_desc": "Central district"
    }
  ]
}
```

---

### 18. `get_pollens.php` — Get Pollen Types Catalog

**Response (`PollenData`):**
```json
{
  "pollens": [
    {
      "id": 3,
      "desc": "Берёза",
      "desc_eng": "Birch",
      "info": "Detailed info in Russian",
      "info_eng": "Detailed info in English",
      "max_level": 5,
      "levels": [
        {
          "level": 1,
          "name": "Низкий",
          "name_eng": "Low",
          "info": "Info text",
          "info_eng": "Info text eng",
          "color": -16711936
        }
      ]
    }
  ]
}
```

**Notes:** `color` is an Android color int (ARGB). `levels` array defines the severity scale for this pollen type.

---

### 19. `get_cures.php` — Get Medications Catalog

**Response (`CuresData`):**
```json
{
  "action_types": [
    { "id": 1, "name_rus": "Антигистаминные", "name_eng": "Antihistamines", "sort_number": 1 }
  ],
  "cures": [
    {
      "id": "10",
      "name_rus": "Цетиризин",
      "name_eng": "Cetirizine",
      "desc_rus": "Описание",
      "desc_eng": "Description",
      "forma": "tablets",
      "sort_number": 1,
      "info_rus": "Информация",
      "info_eng": "Information",
      "action_type": 1,
      "items": [
        {
          "id": "101",
          "name_rus": "Зиртек",
          "name_eng": "Zyrtec",
          "desc_rus": "Описание",
          "desc_eng": "Description",
          "forma": "tablets",
          "sort_number": 1,
          "mark": "Zyrtec",
          "active_substance": "Cetirizine"
        }
      ]
    }
  ],
  "forms": [
    { "id": "1", "name_rus": "Таблетки", "name_eng": "Tablets" }
  ],
  "doses": [
    { "id": "1", "name_rus": "5мг", "name_eng": "5mg" }
  ],
  "frequency": [
    { "id": "1", "name_rus": "Раз в день", "name_eng": "Once daily" }
  ]
}
```

---

### 20. `get_hashtags.php` — Get Map Hashtags

**Response (`HashTagData`):**
```json
{
  "hashtags": [
    { "id": "1", "value": "birch_high", "name": "Birch High" }
  ]
}
```

---

### 21. `get_screen_banners.php` — Get Promotional Banners

**Response (`GetScreenBannersData`):**
```json
{
  "banners": [
    {
      "id": 1,
      "image_url": "https://example.com/banner.jpg",
      "site_url": "https://example.com/promo",
      "type": 1,
      "start_date": 1713100000,
      "end_date": 1713900000,
      "page": "main",
      "position": 1,
      "count_in_day": 3,
      "duration": 5
    }
  ]
}
```

**Notes:** `image_url` loaded via Glide. `site_url` opened on click. `count_in_day` limits daily impressions. `duration` is display time in seconds.

---

## External API Endpoints

### 22. Allergen Forecast Polygons

**URL:** `https://api.pollen.club/static/forecasts/{allergen}/{dateTime}.json`

**Path params:**
- `{allergen}` — allergen code string
- `{dateTime}` — date/time string

**Response (`List<PolygonData>`):**
```json
[
  {
    "latlngs": [[[[55.75, 37.61], [55.76, 37.62], [55.74, 37.63]]]],
    "color": "#FF0000",
    "opacity": 0.8,
    "weight": 1.0,
    "fillColor": "#FF0000",
    "fillOpacity": 0.3
  }
]
```

**Notes:** `latlngs` is a deeply nested array: `List<List<List<List<Double>>>>` representing multi-polygon coordinates. Rendered as polygon overlays on Google Maps.

---

### 23. Dark Sky Weather API (**DEAD**)

**URL:** `https://api.forecast.io/forecast/9a91a6bdaf34c63238d2e6f7813b77bb/{lat},{lon}?units=si`

**Hardcoded API key:** `9a91a6bdaf34c63238d2e6f7813b77bb`

**Response (`ForecastData`):**
```json
{
  "currently": {
    "summary": "Partly Cloudy",
    "icon": "partly-cloudy-day",
    "temperature": 18.5
  }
}
```

**Notes:** Dark Sky was acquired by Apple and the API was sunset on March 31, 2023. This endpoint no longer works. Needs replacement (Open-Meteo, OpenWeatherMap, etc.).

---

## Incremental Sync Pattern

Several endpoints use a `from_id` pattern for incremental data loading:
- `get_levels.php?from_id={id}` — fetch pollen levels newer than ID
- `get_forecasts.php` body `{from_id}` — fetch forecasts newer than ID
- `get_statistics.php` body `{from_id}` — fetch stats newer than ID

The app stores the last known IDs in the `update_info` SQLite table (`last_archives_id`, `last_prognosis_id`, `last_summary_health_id`).
