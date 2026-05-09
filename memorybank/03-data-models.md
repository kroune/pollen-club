# Data Models — Complete API Request/Response Types

All models are in package `pollen.sgolovanov.parsers`.
JSON field names shown in `@SerializedName("...")` format.

---

## Base Types

### BaseResponse
```
{
  "result": boolean,
  "message": String
}
```
Parent class for most responses.

### SetUserResponse (extends BaseResponse)
```
{
  "result": boolean,
  "message": String,
  "user_id": long
}
```

---

## Request Models

### GetUserRequest
```
{ "user_id": long }
```
Used by: `check_ads_for_user`, `get_comments_and_lenta`, `get_friends`, `get_pins_with_friends`

### SetUserRequest
```
{
  "id": long,            // server_id (0 on first registration)
  "name": String,
  "last_name": String,
  "location": int,       // location ID
  "ages": int,
  "activity": int
}
```

### AddUserFeelRequest
```
{
  "date": String,
  "location": int,
  "time": long,          // Unix timestamp
  "opinion": int,        // 0=good, 1=middle, 2=bad (likely)
  "opinion_old": int,
  "latitude": double,
  "longitude": double,
  "user_id": long,
  "default_pollen": int, // selected pollen type ID
  "tags": String,        // comma-separated allergen tags
  "location_name": String
}
```

### AddUserFeelAndSymptomsRequest (for medications)
```
{
  "user_id": long,
  "date": String,
  "cure_id": int,
  "cure_name": String,
  "forma_id": int,
  "forma": String,
  "frequency_id": int,
  "frequency": String,
  "use_from": String,       // date when started taking medication
  "dose_id": int,
  "dose": String,
  "dose_value": int,
  "frequency_value": int,
  "type": int
}
```

### AddUserSymptomsRequest
```
{
  "user_id": long,
  "symptoms": [              // @Expose annotated array
    {
      "date": String,
      "tags": String,
      "nose": int,           // severity 0-N
      "throat": int,
      "eyes": int,
      "general": int,
      "lunds": int,          // "lungs" (typo in original)
      "other": String        // free text
    }
  ]
}
```

### AddFenologyRequest
```
{
  "user_id": long,
  "date": String,
  "time": long,
  "comment": String,
  "state": int,             // phenological state code
  "latitude": double,
  "longitude": double
}
```

### AddFriendRequest
```
{
  "user_id": long,          // Java field: "ussrId", serialized as "user_id"
  "friend_id": int
}
```

### DeleteFriendRequest
```
{
  "user_id": long,
  "friend_id": int
}
```

### CheckAndroidVersionRequest
```
{
  "build": int              // version code (68 for v6.3.6)
}
```

### GetStatisticsRequest
```
{
  "from_id": long           // last known stats ID for incremental sync
}
```

### GetUserForecastsRequest
```
{
  "user_id": long
}
```

### LevelForecastRequest
```
{
  "from_id": int            // last known forecast ID for incremental sync
}
```

---

## Response Models

### CheckAdsData
```
{
  "result": boolean,
  "show_banners": boolean,
  "show_counter": int       // default: 5, screens between interstitials
}
```

### CheckAndroidVersionData
```
{
  "is_old_version": boolean
}
```

### AddUserFeelData
```
{
  "result": Boolean?,
  "message": String?,
  "user_id": int,
  "statistics": {
    "id": int,
    "date": String?,
    "location": int,
    "good": int,
    "middle": int,
    "bad": int
  }
}
```

### AddUserFeelAndSymptomsData
```
{
  "result": Boolean?,
  "message": String?,
  "user_id": long
}
```

### PollenData
```
{
  "pollens": [                    // @SerializedName("pollens")
    {
      "id": int,
      "desc": String,             // Russian name
      "desc_eng": String,         // English name
      "info": String,             // Russian description
      "info_eng": String,         // English description
      "max_level": int,
      "levels": [
        {
          "level": int,           // severity level number
          "name": String,         // Russian name
          "name_eng": String,     // English name
          "info": String,
          "info_eng": String,
          "color": int            // Android ARGB color int
        }
      ]
    }
  ]
}
```

### LevelData
```
{
  "result": [                     // @SerializedName("result")
    {
      "id": int,
      "date": String,
      "pollen": int,              // pollen type ID
      "location": int,            // location ID
      "value": int                // level value
    }
  ]
}
```

### LevelForecastData
```
{
  "levels": [                     // @SerializedName("levels")
    {
      "id": int,
      "date": String,
      "pollen": int,
      "location": int,
      "value": int
    }
  ]
}
```

### LocationData
```
{
  "locations": [                  // @SerializedName("locations")
    {
      "id": int,
      "desc": String,             // Russian name
      "comment": String,          // Russian description
      "latitude": double,
      "longitude": double,
      "eng_name": String,
      "eng_desc": String
    }
  ]
}
```

### StatisticsData
```
{
  "statistics": [                 // @SerializedName("statistics")
    {
      "id": int,
      "date": String,
      "location": int,
      "good": int,                // count of "good" reports
      "middle": int,              // count of "middle" reports
      "bad": int                  // count of "bad" reports
    }
  ]
}
```

### CommentsData
```
{
  "comments": [
    {
      "id": int,
      "date": String,
      "expert": int,              // expert ID
      "comment": String,          // Russian text
      "locations_id": int,
      "comment_eng": String,
      "pin": int                  // pinned flag (0/1)
    }
  ],
  "lenta_vk": [
    {
      "id": int,
      "date": String,
      "location": String,
      "information": String,      // VK post content
      "pin": int
    }
  ],
  "lenta_media": [
    {
      "id": int,
      "date": String,
      "media_type": String,       // "image" or "video"
      "url": String,              // media URL (loaded via Glide/ExoPlayer)
      "info_rus": String,
      "info_eng": String,
      "pin": int
    }
  ],
  "friends": [
    {
      "id": int,
      "location": String,
      "date": String,
      "time": int,
      "friend_id": int,
      "opinion": int              // friend's feeling (good/middle/bad)
    }
  ]
}
```

### PinsData
```
{
  "pins": [                       // @SerializedName("pins")
    {
      "date": String,
      "value": int,               // feeling value
      "latitude": double,
      "longitude": double,
      "pollen_type": int,
      "tags": String,
      "friend_id": int            // 0 = own pin, >0 = friend's pin
    }
  ]
}
```

### FriendsData
```
{
  "friends": [                    // @SerializedName("friends")
    {
      "id": int,
      "friend_id": int
    }
  ]
}
```

### HashTagData
```
{
  "hashtags": [                   // @SerializedName("hashtags")
    {
      "id": String,
      "value": String,
      "name": String?
    }
  ]
}
```

### CuresData
```
{
  "action_types": [
    {
      "id": int,
      "name_rus": String?,
      "name_eng": String?,
      "sort_number": int
    }
  ],
  "cures": [
    {
      "id": String,
      "name_rus": String?,
      "name_eng": String?,
      "desc_rus": String?,
      "desc_eng": String?,
      "forma": String?,
      "sort_number": int,
      "info_rus": String?,
      "info_eng": String?,
      "action_type": int,
      "items": [
        {
          "id": String,
          "name_rus": String?,
          "name_eng": String?,
          "desc_rus": String?,
          "desc_eng": String?,
          "forma": String?,
          "sort_number": int,
          "mark": String?,              // brand name
          "active_substance": String?   // active ingredient
        }
      ]
    }
  ],
  "forms": [
    { "id": String, "name_rus": String?, "name_eng": String? }
  ],
  "doses": [
    { "id": String, "name_rus": String?, "name_eng": String? }
  ],
  "frequency": [
    { "id": String, "name_rus": String?, "name_eng": String? }
  ]
}
```

### UserForecastData
```
{
  "result": {
    "user_info": [
      {
        "id": String?,
        "desc_rus": String?,
        "desc_eng": String?
      }
    ],
    "user_forecast": [
      {
        "id": String?,
        "date": String?,
        "value": String?
      }
    ]
  }
}
```

### GetScreenBannersData
```
{
  "banners": [
    {
      "id": long,
      "image_url": String,        // banner image URL
      "site_url": String,         // click-through URL
      "type": int,
      "start_date": long,         // Unix timestamp
      "end_date": long,
      "page": String,             // target page/screen
      "position": int,
      "count_in_day": int,        // max daily impressions
      "duration": int             // display duration seconds
    }
  ]
}
```

### ForecastData (Dark Sky weather — external)
```
{
  "currently": {
    "summary": String,
    "icon": String,
    "temperature": double         // Celsius (units=si)
  }
}
```

### PolygonData (allergen forecast polygons — external)
```
{
  "latlngs": List<List<List<List<Double>>>>,  // multi-polygon coordinates
  "color": String,                             // hex color
  "opacity": float,
  "weight": float,
  "fillColor": String,
  "fillOpacity": float
}
```

---

## Unused/Legacy Models (exist but not in ApiService)

These classes exist in the parsers package but aren't referenced in the current Retrofit interfaces:

- `PinData` — `{result: [{date, value, latitude, longitude, pollen_type}]}` (strings, not typed)
- `CommentData` — standalone comment model
- `SetFeelAndPinData` — `{status: int, result: [{complete: int, user_id: String}]}`
- `SetUserData` — `{result: [{result_id: int}]}`
- `WeatherData` — `{result: [{id, date, location, is_rain}]}`
- `AddUserSymptomsData` — unused symptoms response
