# Pollen Club v6.3.6 — Project Overview

## App Identity

| Field | Value |
|---|---|
| Package name | `pollen.sgolovanov.pollen2` |
| Application ID | `pollen.sgolovanov.pollen2` |
| Version name | `6.3.6` |
| Version code | `68` |
| Min SDK | 24 (Android 7.0 Nougat) |
| Target SDK | 35 (Android 15) |
| Compile SDK | 35 |
| Language | Kotlin (with coroutines) |
| Application class | `pollen.sgolovanov.pollen2.PollenApplication` |
| Build type | Release |

## What the App Does

Pollen Club is a pollen monitoring app for allergy sufferers. Core features:

1. **Pollen Level Monitoring** — displays current pollen levels at monitoring stations
2. **Allergen Forecast Map** — polygon overlays on Google Maps showing predicted pollen spread
3. **Health Diary** — users log how they feel (good/middle/bad), symptoms (eyes, nose, throat, lungs, general), and medications
4. **Phenology Observations** — users submit plant flowering observations with GPS coordinates
5. **Expert Comments & Feed** — expert analysis + VK social feed + media content
6. **Friends** — users can add friends and see their health pins on the map
7. **Medication Tracker** — catalog of allergy medications with dosage/frequency tracking
8. **Weather Integration** — weather data overlay (Dark Sky API — now dead)
9. **Statistics** — aggregated health statistics per location
10. **Promotional Banners** — server-driven banner ads

## Server Domains

| Domain | Purpose |
|---|---|
| `data.pollen.club` | Main REST API (21 PHP endpoints) |
| `api.pollen.club` | Static allergen forecast polygon JSON files |
| `api.forecast.io` | Weather data (Dark Sky — **DEPRECATED/DEAD**) |
| `pollen.club` | Website (WebView pages, social links) |

## Decompiled Source Locations

| Path | Contents |
|---|---|
| `decompiled/jadx/` | Java source files (recovered by jadx) |
| `decompiled/apktool/` | Resources, smali, AndroidManifest.xml |

## Architecture Red Flags (Original App)

1. **God Activity** — `MainActivity` is ~2000+ lines, handles everything
2. **No dependency injection** — `RestClient` singleton, `MainActivity.INSTANCE` as service locator
3. **No repository pattern** — API calls made directly from Activities/Fragments
4. **No data mappers** — API DTOs used directly in UI
5. **Raw SQLite** with manual cursor reads — no Room
6. **Dead Dark Sky API** — forecast.io was sunset by Apple
7. **Cleartext traffic enabled globally** — no HTTPS enforcement
8. **No error handling/retry** — network failures silently crash coroutines
9. **Bilingual hardcoding** — `_rus`/`_eng` field pairs throughout all models
10. **No push notifications** — app has no way to notify users of high pollen events
11. **3-minute HTTP timeouts** — excessively long
12. **No SSL pinning** — no certificate pinning configured
