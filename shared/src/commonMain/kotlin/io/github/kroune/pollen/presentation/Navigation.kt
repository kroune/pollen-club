package io.github.kroune.pollen.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import dev.icerock.moko.resources.StringResource
import io.github.kroune.pollen.MR
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
@SerialName("home")
data object HomeRoute : NavKey

@Serializable
@SerialName("map")
data object MapRoute : NavKey

@Serializable
@SerialName("diary")
data object DiaryRoute : NavKey

@Serializable
@SerialName("feed")
data object FeedRoute : NavKey

@Serializable
@SerialName("settings")
data object SettingsRoute : NavKey

@Serializable
@SerialName("medications")
data object MedicationsRoute : NavKey

@Serializable
@SerialName("phenology")
data object PhenologyRoute : NavKey

@Serializable
@SerialName("settings_profile")
data object SettingsProfileRoute : NavKey

@Serializable
@SerialName("settings_allergens")
data object SettingsAllergensRoute : NavKey

@Serializable
@SerialName("settings_locations")
data object SettingsLocationsRoute : NavKey

@Serializable
@SerialName("settings_language")
data object SettingsLanguageRoute : NavKey

@Serializable
@SerialName("settings_friends")
data object SettingsFriendsRoute : NavKey

@Serializable
@SerialName("add_friend")
data object AddFriendRoute : NavKey

@Serializable
@SerialName("my_qr")
data object MyQrRoute : NavKey

@Serializable
@SerialName("reference")
data object ReferenceRoute : NavKey

@Serializable
@SerialName("forecast_detail")
data class ForecastDetailRoute(val pollenId: Int) : NavKey

data class BottomNavItem(
    val route: NavKey,
    val labelRes: StringResource,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(HomeRoute, MR.strings.nav_forecast, Icons.Default.Home),
    BottomNavItem(DiaryRoute, MR.strings.nav_diary, Icons.Default.SentimentSatisfied),
    BottomNavItem(MapRoute, MR.strings.nav_map, Icons.Default.LocationOn),
    BottomNavItem(FeedRoute, MR.strings.nav_feed, Icons.Default.ChatBubbleOutline),
    BottomNavItem(PhenologyRoute, MR.strings.nav_phenology, Icons.Default.Eco),
)

val navSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(HomeRoute::class, HomeRoute.serializer())
        subclass(MapRoute::class, MapRoute.serializer())
        subclass(DiaryRoute::class, DiaryRoute.serializer())
        subclass(FeedRoute::class, FeedRoute.serializer())
        subclass(SettingsRoute::class, SettingsRoute.serializer())
        subclass(MedicationsRoute::class, MedicationsRoute.serializer())
        subclass(PhenologyRoute::class, PhenologyRoute.serializer())
        subclass(SettingsProfileRoute::class, SettingsProfileRoute.serializer())
        subclass(SettingsAllergensRoute::class, SettingsAllergensRoute.serializer())
        subclass(SettingsLocationsRoute::class, SettingsLocationsRoute.serializer())
        subclass(SettingsLanguageRoute::class, SettingsLanguageRoute.serializer())
        subclass(SettingsFriendsRoute::class, SettingsFriendsRoute.serializer())
        subclass(AddFriendRoute::class, AddFriendRoute.serializer())
        subclass(MyQrRoute::class, MyQrRoute.serializer())
        subclass(ReferenceRoute::class, ReferenceRoute.serializer())
        subclass(ForecastDetailRoute::class, ForecastDetailRoute.serializer())
    }
}
