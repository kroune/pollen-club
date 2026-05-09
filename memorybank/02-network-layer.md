# Network Layer Architecture

## HTTP Stack

- **HTTP Client:** OkHttp 3
- **REST Client:** Retrofit 2
- **Call Adapter:** RxJava2CallAdapterFactory (registered but actual API methods use Kotlin coroutine suspend functions)
- **Serialization:** Gson via GsonConverterFactory
- **Async:** Kotlin Coroutines (suspend functions, `Dispatchers.IO`)

## RestClient Singleton

File: `pollen/sgolovanov/restapi/RestClient.java`

```kotlin
object RestClient {
    val apiService: ApiService
    val apiOutsideService: ApiOutsideService

    init {
        val gson = GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Long::class.java, LongTypeAdapter())
            .create()

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(RequestInterceptor())  // NO-OP
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://data.pollen.club/api_2/")
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)
        apiOutsideService = retrofit.create(ApiOutsideService::class.java)
    }
}
```

## OkHttp Configuration

| Setting | Value |
|---|---|
| Connect timeout | 3 minutes |
| Read timeout | 3 minutes |
| Write timeout | 3 minutes |
| SSL Pinning | **None** |
| Certificate Pinner | **None** |
| Custom SSLSocketFactory | **None** |
| Custom HostnameVerifier | **None** |
| Proxy | **None** |
| HTTP Cache | **None** |
| Authenticator | **None** |
| Connection pool | Default |

## Interceptor — RequestInterceptor (NO-OP)

File: `pollen/sgolovanov/restapi/RequestInterceptor.java`

```kotlin
class RequestInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {
        return chain.proceed(chain.request().newBuilder().build())
    }
}
```

This interceptor does absolutely nothing — it rebuilds the request identically and proceeds. No headers added, no auth tokens, no logging. It's a placeholder/stub.

Note: `HttpLoggingInterceptor` class IS bundled in the APK but never used.

## Gson Configuration

```kotlin
val gson = GsonBuilder()
    .serializeNulls()           // null fields ARE included in JSON
    .registerTypeAdapter(
        Long::class.java,
        LongTypeAdapter()       // safe Long parsing
    )
    .create()
```

### LongTypeAdapter

Custom adapter that handles the server sometimes returning Long values as null or unparseable strings:

```kotlin
class LongTypeAdapter : JsonDeserializer<Long>, JsonSerializer<Long> {
    override fun deserialize(json: JsonElement, ...): Long {
        return try {
            json.asLong
        } catch (e: Exception) {
            0L  // returns 0 for null/unparseable
        }
    }
}
```

## Request/Response Annotations

- Request body fields use `@SerializedName("json_key")` and/or `@Expose`
- Response fields use `@SerializedName("json_key")`
- Some response wrapper classes use `@SerializedName` on the top-level list field (e.g., `@SerializedName("pollens") List<Pollen> pollens`)

## Error Handling

**Virtually none:**

- No retry interceptor
- No Retrofit retry adapter
- No global error handler
- Coroutine exceptions are mostly unhandled (crash silently)
- One exception: `MyMapFragment.updatePolygons` wraps its call in try/catch with `Log.e()`

**UI-level error display:**
- `BaseMvpView.onError(message)` — shows alert dialog
- `BaseMvpView.onNetworkError()` — shows "Network Error"
- `BaseMvpView.isNetworkConnected()` — broken implementation, just returns `getActivity() != null`

## Network Security Config

File: `res/xml/network_security_config.xml`

```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">127.0.0.1</domain>
    </domain-config>
</network-security-config>
```

Also in AndroidManifest: `android:usesCleartextTraffic="true"`

## Who Calls What

| Caller | API Methods |
|---|---|
| `MainActivity` | `setUser`, `checkAds`, `getScreenBanners`, `getPollens`, `getForecastData`, `addUserFeel`, `getHashTags`, `checkAndroidVersion`, `addUserSymptoms` |
| `MyMapFragment` | `getPins`, `getAllergenForecast` (polygon data) |
| `CentralFragment` | `getLocations` |
| `SymptomsFragment` | `getStatistics`, `getCures` |
| `CureActivity` | `addUserCure` |
| `CommentsFragment` | `getComments` |
| `PhenologiesFragment` | `addPhenology` |
| `FriendsActivity` | `getFriends`, `addFriend`, `deleteFriend` |
| `LocationsActivity` | `getLocations` |

## Data Flow

```
Activity/Fragment
    → RestClient.INSTANCE.getApiService().<method>()
    → Retrofit (suspend function, Dispatchers.IO)
    → OkHttp (RequestInterceptor NO-OP)
    → Server (data.pollen.club/api_2/*.php)
    → JSON Response
    → Gson deserialization → *Data classes
    → Manual SQLite insert (raw SQL)
    → UI update
```

There is NO repository layer, NO use cases, NO data mappers. API DTOs are used directly.
