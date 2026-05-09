# Local Database Schema (SQLite)

## Database Info

| Field | Value |
|---|---|
| Name | `"Pollen"` |
| Version | 9 |
| Helper class | `pollen.sgolovanov.database.PollenSQLiteHelper` |
| ORM | **None** — raw SQLite with manual cursor reads |

---

## Table: `users`

Stores the local user profile. Typically one row.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | Local ID |
| `name` | TEXT | First name |
| `last_name` | TEXT | Last name |
| `social_web` | INTEGER | Social network ID |
| `location` | INTEGER | Selected location ID |
| `default_pollen` | INTEGER | Selected default allergen type ID |
| `is_bereza` | INTEGER | Birch enabled (1/0) |
| `is_dub` | INTEGER | Oak enabled (1/0) |
| `is_polyn` | INTEGER | Wormwood enabled (1/0) |
| `is_olkha` | INTEGER | Alder enabled (1/0) |
| `is_oreshnik` | INTEGER | Hazel enabled (1/0) |
| `is_zlaki` | INTEGER | Grasses enabled (1/0) |
| `is_marevye` | INTEGER | Goosefoot enabled (1/0) |
| `is_ambrosia` | INTEGER | Ragweed enabled (1/0) |
| `photo` | TEXT | Photo path/URL |
| `sex` | INTEGER | Gender |
| `years` | INTEGER | Age |
| `hide_comment` | INTEGER | Hide comments flag |
| `activity` | INTEGER | Activity level |
| `hide_facebook` | INTEGER | Hide Facebook flag |
| `hide_vkontakte` | INTEGER | Hide VK flag |
| `server_id` | INTEGER | **Server-assigned user ID** — used in all API calls |
| `quest_num` | INTEGER | Quest/survey number |
| `quest_position` | INTEGER | Quest position |
| `points` | INTEGER | User points |
| `anketa_result` | TEXT | Survey result text |
| `is_anketa_sent` | INTEGER | Survey sent flag |
| `anketa_score` | INTEGER | Survey score |
| `quest_comment` | TEXT | Quest comment |
| `prev_changed_pollen` | INTEGER | Previously changed pollen type |
| `prev_changed_num` | INTEGER | Previous change count |

**Allergen columns mapping:**

| Column | Russian Name | English Name |
|---|---|---|
| `is_bereza` | Берёза | Birch |
| `is_dub` | Дуб | Oak |
| `is_polyn` | Полынь | Wormwood |
| `is_olkha` | Ольха | Alder |
| `is_oreshnik` | Орешник | Hazel |
| `is_zlaki` | Злаки | Grasses |
| `is_marevye` | Маревые | Goosefoot |
| `is_ambrosia` | Амброзия | Ragweed |

---

## Table: `update_info`

Tracks sync state for incremental data loading. One row.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `last_update_date` | INTEGER | Last sync timestamp |
| `last_archives_id` | INTEGER | Last pollen levels ID fetched (for `get_levels.php?from_id=`) |
| `last_prognosis_id` | INTEGER | Last forecast ID fetched (for `get_forecasts.php`) |
| `last_summary_health_id` | INTEGER | Last statistics ID fetched (for `get_statistics.php`) |
| `last_user_id` | INTEGER | Last user data sync |
| `last_comment` | TEXT | Last comment text |
| `last_expert_id` | INTEGER | Last expert comment ID |
| `last_comment_date` | INTEGER | Last comment date |
| `latitude` | FLOAT | Last known latitude |
| `longitude` | FLOAT | Last known longitude |
| `url_map` | TEXT | Dynamic map URL (default: `https://pollen.club/maps/map.html`) |
| `url_news` | TEXT | Dynamic news URL (default: `https://pollen.club`) |

---

## Table: `health`

User health diary entries.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `user_id` | INTEGER | |
| `date` | INTEGER | Timestamp |
| `value` | INTEGER | Overall feeling (opinion value) |
| `eyes_symptoms` | INTEGER | Eyes severity |
| `nose_symptoms` | INTEGER | Nose severity |
| `throat_symptoms` | INTEGER | Throat severity |
| `lunds_symptoms` | INTEGER | Lungs severity ("lunds" = typo) |
| `general_symptoms` | INTEGER | General severity |
| `eyes_comment` | TEXT | |
| `nose_comment` | TEXT | |
| `throat_comment` | TEXT | |
| `other_comment` | TEXT | Free text other symptoms |
| `time` | INTEGER | Time of entry |
| `local_area_name` | TEXT | Location name string |
| `sended_server` | INTEGER | 0 = not synced, 1 = synced to server |

---

## Table: `comments`

Expert comments cached locally.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `expert_id` | INTEGER | |
| `date` | INTEGER | |
| `time` | INTEGER | |
| `comment` | TEXT | Russian text |
| `location_id` | INTEGER | |
| `comment_eng` | TEXT | English text |

---

## Table: `summary_health`

Aggregated health statistics per location/date.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `location_id` | INTEGER | |
| `date` | INTEGER | |
| `bad` | INTEGER | Count of bad reports |
| `middle` | INTEGER | Count of middle reports |
| `good` | INTEGER | Count of good reports |

---

## Table: `locations`

Pollen monitoring stations.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `server_ID` | INTEGER | Server-assigned location ID |
| `name` | TEXT | Russian name |
| `comment` | TEXT | Russian description |
| `eng_name` | TEXT | English name |
| `eng_comment` | TEXT | English description |
| `latitude` | FLOAT | |
| `longitude` | FLOAT | |

---

## Table: `weather_prognosis`

Weather forecast data (from Dark Sky).

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `date` | INTEGER | |
| `location_id` | INTEGER | |
| `is_rain` | INTEGER | Rain flag (0/1) |

---

## Table: `fenologies`

User-submitted phenology observations.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `date` | INTEGER | |
| `time` | INTEGER | |
| `state` | INTEGER | Phenological state code |
| `latitude` | FLOAT | |
| `longitude` | FLOAT | |
| `location` | TEXT | Location name |
| `comment` | TEXT | User's comment |

---

## Table: `day_activities`

Daily user engagement tracking.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `date` | INTEGER | |
| `application_opened` | INTEGER | Opened app flag |
| `health_set` | INTEGER | Set health today flag |
| `symptoms_set` | INTEGER | Set symptoms today flag |
| `map_opened` | INTEGER | Opened map flag |
| `experts_read` | INTEGER | Read expert comments flag |

---

## Table: `pollens`

Pollen type catalog.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `server_id` | INTEGER | |
| `desc` | TEXT | Russian name |
| `desc_eng` | TEXT | English name |
| `info` | TEXT | Russian description |
| `info_eng` | TEXT | English description |
| `max_level` | INTEGER | Maximum severity level |

---

## Table: `levels_info`

Severity level definitions per pollen type.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `pollen_id` | INTEGER | FK to pollens |
| `level` | INTEGER | Level number |
| `name` | TEXT | Russian name |
| `name_eng` | TEXT | English name |
| `info` | TEXT | Russian description |
| `info_eng` | TEXT | English description |
| `color` | INTEGER | ARGB color int |

---

## Table: `levels`

Actual pollen level measurements.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `pollen_types_id` | INTEGER | FK to pollens |
| `locations_id` | INTEGER | FK to locations |
| `date` | INTEGER | |
| `value` | INTEGER | Level value |
| `prognosis` | INTEGER | Is forecast flag (0=actual, 1=forecast) |

---

## Table: `post_message`

Queued messages (possibly for offline sync).

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `message` | TEXT | |

---

## Table: `therapies`

User's medication/therapy tracking.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `date` | INTEGER | |
| `cureTypeID` | INTEGER | Action type ID |
| `cure` | TEXT | Cure name |
| `cureId` | INTEGER | Cure ID |
| `form` | TEXT | Form name (tablets, drops, etc.) |
| `formId` | INTEGER | Form ID |
| `doseValue` | INTEGER | |
| `dose` | TEXT | Dose name |
| `doseId` | INTEGER | |
| `frequencyValue` | INTEGER | |
| `frequency` | TEXT | Frequency name |
| `frequencyId` | INTEGER | |
| `startDate` | INTEGER | When started taking |

---

## Table: `Friends`

User's friends list (note: capital F in table name).

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `server_id` | INTEGER | Friend's server user ID |
| `name` | TEXT | Friend's name |

---

## Table: `lenta_vk`

VK social network feed items.

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `date` | TEXT | Date string |
| `information` | TEXT | Post content |
| `location` | TEXT | Location name |

---

## SharedPreferences

Preferences file name: `"SHARED_PREFS_NAME"` (literal string)

| Key | Type | Purpose |
|---|---|---|
| `ACCEPTED_KEY` | boolean | Terms/EULA accepted |
| `FIRST_RUN_KEY` | boolean | First launch detection |
| `LANGUAGE_CODE` | String | Selected language code |
| `IS_SYMPTOMS_UPLOAD_TO_SERVER` | boolean | Historical symptoms synced |
| `CURRENT_PAGE_OPENED` | int | Counter for interstitial ad pacing |
| `tags` | StringSet | Cached user tag selections |
| `CURE_SCHEME_<id>` | String | Per-cure scheme data (keyed by cure ID) |
| `BANNER_<id>_<date>` | int | Per-banner daily impression count |

Separate preferences file: `"google_bug_154855417"` — workaround for known Android bug in `PollenApplication`.
