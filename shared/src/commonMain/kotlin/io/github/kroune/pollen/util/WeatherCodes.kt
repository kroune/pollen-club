package io.github.kroune.pollen.util

import dev.icerock.moko.resources.StringResource
import io.github.kroune.pollen.MR

fun weatherCodeToResource(code: Int): StringResource = when (code) {
    0 -> MR.strings.weather_clear_sky
    1 -> MR.strings.weather_mainly_clear
    2 -> MR.strings.weather_partly_cloudy
    3 -> MR.strings.weather_overcast
    45, 48 -> MR.strings.weather_foggy
    51, 53, 55 -> MR.strings.weather_drizzle
    56, 57 -> MR.strings.weather_freezing_drizzle
    61, 63, 65 -> MR.strings.weather_rain
    66, 67 -> MR.strings.weather_freezing_rain
    71, 73, 75 -> MR.strings.weather_snow
    77 -> MR.strings.weather_snow_grains
    80, 81, 82 -> MR.strings.weather_rain_showers
    85, 86 -> MR.strings.weather_snow_showers
    95 -> MR.strings.weather_thunderstorm
    96, 99 -> MR.strings.weather_thunderstorm_hail
    else -> MR.strings.weather_unknown
}
