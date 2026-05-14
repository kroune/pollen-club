package io.github.kroune.pollen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import io.github.kroune.pollen.presentation.DiaryRoute
import io.github.kroune.pollen.presentation.FeedRoute
import io.github.kroune.pollen.presentation.ForecastDetailRoute
import io.github.kroune.pollen.presentation.HomeRoute
import io.github.kroune.pollen.presentation.MapRoute
import io.github.kroune.pollen.presentation.MedicationsRoute
import io.github.kroune.pollen.presentation.PhenologyRoute
import io.github.kroune.pollen.presentation.ReferenceRoute
import io.github.kroune.pollen.presentation.SettingsAllergensRoute
import io.github.kroune.pollen.presentation.SettingsFriendsRoute
import io.github.kroune.pollen.presentation.SettingsLanguageRoute
import io.github.kroune.pollen.presentation.SettingsLocationsRoute
import io.github.kroune.pollen.presentation.SettingsRoute
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.presentation.bottomNavItems
import io.github.kroune.pollen.presentation.theme.PollenTheme
import io.github.kroune.pollen.presentation.diary.DiaryScreen
import io.github.kroune.pollen.presentation.feed.FeedScreen
import io.github.kroune.pollen.presentation.home.ForecastDetailScreen
import io.github.kroune.pollen.presentation.home.ForecastDetailViewModel
import io.github.kroune.pollen.presentation.home.HomeScreen
import io.github.kroune.pollen.presentation.map.MapScreen
import io.github.kroune.pollen.presentation.medications.MedicationsScreen
import io.github.kroune.pollen.presentation.navSerializersModule
import io.github.kroune.pollen.presentation.phenology.PhenologyScreen
import io.github.kroune.pollen.presentation.reference.ReferenceScreen
import io.github.kroune.pollen.presentation.sensitivity.SensitivityScreen
import io.github.kroune.pollen.presentation.settings.SettingsLanguageScreen
import io.github.kroune.pollen.presentation.AddFriendRoute
import io.github.kroune.pollen.presentation.friends.AddFriendScreen
import io.github.kroune.pollen.presentation.friends.FriendsListScreen
import io.github.kroune.pollen.presentation.settings.RegionSelectorScreen
import io.github.kroune.pollen.presentation.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    PollenTheme {
        val savedStateConfig = SavedStateConfiguration {
            serializersModule = navSerializersModule
        }
        val backStack = rememberNavBackStack(savedStateConfig, HomeRoute)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = PollenTheme.colors.card,
                    contentColor = PollenTheme.colors.ink,
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PollenTheme.colors.accent2,
                        selectedTextColor = PollenTheme.colors.accent2,
                        unselectedIconColor = PollenTheme.colors.ink3,
                        unselectedTextColor = PollenTheme.colors.ink3,
                        indicatorColor = PollenTheme.colors.accentLight,
                    )
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = backStack.lastOrNull() == item.route,
                            onClick = {
                                if (backStack.lastOrNull() != item.route) {
                                    backStack.removeAll { it != backStack.first() }
                                    if (item.route != HomeRoute) {
                                        backStack.add(item.route)
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                            label = { Text(stringResource(item.labelRes)) },
                            colors = navItemColors,
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                modifier = Modifier.fillMaxSize(),
                entryProvider = entryProvider {
                    entry<HomeRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            HomeScreen(
                                onNavigateToForecast = { pollenId ->
                                    backStack.add(ForecastDetailRoute(pollenId))
                                },
                                onNavigateToAllergenSettings = {
                                    backStack.add(SettingsAllergensRoute)
                                },
                                onNavigateToSettings = {
                                    backStack.add(SettingsRoute)
                                },
                            )
                        }
                    }
                    entry<MapRoute> {
                        Box(
                            Modifier
                                .padding(bottom = innerPadding.calculateBottomPadding())
                                .fillMaxSize(),
                        ) {
                            MapScreen()
                        }
                    }
                    entry<DiaryRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            DiaryScreen(
                                onNavigateToMedications = { backStack.add(MedicationsRoute) },
                            )
                        }
                    }
                    entry<FeedRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            FeedScreen(
                                onNavigateToAddFriend = { backStack.add(AddFriendRoute) },
                            )
                        }
                    }
                    entry<SettingsRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            SettingsScreen(
                                onBack = { backStack.removeLastOrNull() },
                                onNavigateToLanguage = { backStack.add(SettingsLanguageRoute) },
                                onNavigateToLocations = { backStack.add(SettingsLocationsRoute) },
                                onNavigateToAllergens = { backStack.add(SettingsAllergensRoute) },
                                onNavigateToFriends = { backStack.add(SettingsFriendsRoute) },
                                onNavigateToReference = { backStack.add(ReferenceRoute) },
                            )
                        }
                    }
                    entry<MedicationsRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            MedicationsScreen(onBack = { backStack.removeLastOrNull() })
                        }
                    }
                    entry<PhenologyRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) { PhenologyScreen() }
                    }
                    entry<SettingsAllergensRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            SensitivityScreen(
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                    entry<SettingsLanguageRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            SettingsLanguageScreen(
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                    entry<SettingsLocationsRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            RegionSelectorScreen(
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                    entry<SettingsFriendsRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            FriendsListScreen(
                                onBack = { backStack.removeLastOrNull() },
                                onNavigateToAddFriend = { backStack.add(AddFriendRoute) },
                            )
                        }
                    }
                    entry<AddFriendRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            AddFriendScreen(
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                    entry<ReferenceRoute> {
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            ReferenceScreen(
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                    entry<ForecastDetailRoute> { route ->
                        val viewModel = koinViewModel<ForecastDetailViewModel>(
                            key = "forecast_${route.pollenId}",
                        ) { parametersOf(route.pollenId) }
                        Box(Modifier.padding(innerPadding).fillMaxSize()) {
                            ForecastDetailScreen(
                                viewModel = viewModel,
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                },
            )
        }
    }
}
