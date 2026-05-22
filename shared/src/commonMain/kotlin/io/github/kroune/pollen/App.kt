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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
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
import io.github.kroune.pollen.presentation.bottomNavItems
import io.github.kroune.pollen.presentation.theme.PollenTheme
import io.github.kroune.pollen.presentation.diary.DiaryScreen
import io.github.kroune.pollen.presentation.feed.FeedScreen
import io.github.kroune.pollen.presentation.detail.ForecastDetailScreen
import io.github.kroune.pollen.presentation.home.HomeScreen
import io.github.kroune.pollen.presentation.map.MapScreen
import io.github.kroune.pollen.presentation.medications.MedicationsScreen
import io.github.kroune.pollen.presentation.navSerializersModule
import io.github.kroune.pollen.presentation.phenology.PhenologyScreen
import io.github.kroune.pollen.presentation.reference.ReferenceScreen
import io.github.kroune.pollen.presentation.sensitivity.SensitivityScreen
import io.github.kroune.pollen.presentation.settings.SettingsLanguageScreen
import io.github.kroune.pollen.presentation.AddFriendRoute
import io.github.kroune.pollen.presentation.MyQrRoute
import io.github.kroune.pollen.presentation.diary.DiaryViewModel
import io.github.kroune.pollen.presentation.feed.FeedIntent
import io.github.kroune.pollen.presentation.feed.FeedViewModel
import io.github.kroune.pollen.presentation.friends.AddFriendScreen
import io.github.kroune.pollen.presentation.friends.AddFriendViewModel
import io.github.kroune.pollen.presentation.friends.FriendsListContent
import io.github.kroune.pollen.presentation.friends.FriendsListScreen
import io.github.kroune.pollen.presentation.friends.FriendsListViewModel
import io.github.kroune.pollen.presentation.friends.MyQrScreen
import io.github.kroune.pollen.presentation.friends.MyQrViewModel
import io.github.kroune.pollen.presentation.friends.MyQrIntent
import io.github.kroune.pollen.presentation.detail.viewmodel.ForecastDetailIntent
import io.github.kroune.pollen.presentation.detail.viewmodel.ForecastDetailViewModel
import io.github.kroune.pollen.presentation.home.HomeViewModel
import io.github.kroune.pollen.presentation.reference.ReferenceIntent
import io.github.kroune.pollen.presentation.map.MapViewModel
import io.github.kroune.pollen.presentation.medications.MedicationsViewModel
import io.github.kroune.pollen.presentation.phenology.PhenologyViewModel
import io.github.kroune.pollen.presentation.reference.ReferenceViewModel
import io.github.kroune.pollen.presentation.sensitivity.SensitivityIntent
import io.github.kroune.pollen.presentation.sensitivity.SensitivityViewModel
import io.github.kroune.pollen.presentation.settings.RegionSelectorScreen
import io.github.kroune.pollen.presentation.settings.RegionSelectorViewModel
import io.github.kroune.pollen.presentation.settings.SettingsIntent
import io.github.kroune.pollen.presentation.settings.SettingsLanguageViewModel
import io.github.kroune.pollen.presentation.settings.SettingsScreen
import io.github.kroune.pollen.presentation.settings.SettingsViewModel
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
            NavGraph(backStack, innerPadding)
        }
    }
}

@Composable
private fun NavGraph(
    backStack: NavBackStack<NavKey>,
    innerPadding: PaddingValues,
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = Modifier.fillMaxSize(),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                val vm: HomeViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    HomeScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onNavigateToForecast = { pollenId ->
                            backStack.add(ForecastDetailRoute(pollenId))
                        },
                        onNavigateToAllergenSettings = { backStack.add(SettingsAllergensRoute) },
                        onNavigateToSettings = { backStack.add(SettingsRoute) },
                    )
                }
            }
            entry<MapRoute> {
                val vm: MapViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(
                    Modifier
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .fillMaxSize(),
                ) {
                    MapScreen(state = state, effects = vm.effects, onIntent = vm::onIntent)
                }
            }
            entry<DiaryRoute> {
                val vm: DiaryViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    DiaryScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onNavigateToMedications = { backStack.add(MedicationsRoute) },
                    )
                }
            }
            entry<FeedRoute> {
                val feedVm: FeedViewModel = koinViewModel()
                val friendsVm: FriendsListViewModel = koinViewModel()
                val feedState by feedVm.state.collectAsState()
                val friendsState by friendsVm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    FeedScreen(
                        state = feedState,
                        effects = feedVm.effects,
                        onRefresh = { feedVm.onIntent(FeedIntent.Refresh) },
                        friendsTabContent = { host ->
                            FriendsListContent(
                                state = friendsState,
                                snackbarHostState = host,
                                effects = friendsVm.effects,
                                onIntent = friendsVm::onIntent,
                                onNavigateToAddFriend = { backStack.add(AddFriendRoute) },
                            )
                        },
                    )
                }
            }
            entry<SettingsRoute> {
                val vm: SettingsViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    SettingsScreen(
                        state = state,
                        effects = vm.effects,
                        onRetry = { vm.onIntent(SettingsIntent.LoadData) },
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
                val vm: MedicationsViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    MedicationsScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
            }
            entry<PhenologyRoute> {
                val vm: PhenologyViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    PhenologyScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                    )
                }
            }
            entry<SettingsAllergensRoute> {
                val vm: SensitivityViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    SensitivityScreen(
                        state = state,
                        effects = vm.effects,
                        onBack = { backStack.removeLastOrNull() },
                        onRetry = { vm.onIntent(SensitivityIntent.LoadData) },
                        onSetSensitivity = { id, level ->
                            vm.onIntent(
                                SensitivityIntent.SetSensitivity(
                                    id,
                                    level,
                                ),
                            )
                        },
                    )
                }
            }
            entry<SettingsLanguageRoute> {
                val vm: SettingsLanguageViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    SettingsLanguageScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
            }
            entry<SettingsLocationsRoute> {
                val vm: RegionSelectorViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    RegionSelectorScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
            }
            entry<SettingsFriendsRoute> {
                val vm: FriendsListViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    FriendsListScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onBack = { backStack.removeLastOrNull() },
                        onNavigateToAddFriend = { backStack.add(AddFriendRoute) },
                    )
                }
            }
            entry<AddFriendRoute> {
                val vm: AddFriendViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    AddFriendScreen(
                        state = state,
                        effects = vm.effects,
                        onIntent = vm::onIntent,
                        onBack = { backStack.removeLastOrNull() },
                        onNavigateToMyQr = { backStack.add(MyQrRoute) },
                    )
                }
            }
            entry<MyQrRoute> {
                val vm: MyQrViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    MyQrScreen(
                        state = state,
                        effects = vm.effects,
                        onBack = { backStack.removeLastOrNull() },
                        onRetry = { vm.onIntent(MyQrIntent.LoadData) },
                    )
                }
            }
            entry<ReferenceRoute> {
                val vm: ReferenceViewModel = koinViewModel()
                val state by vm.state.collectAsState()
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    ReferenceScreen(
                        state = state,
                        effects = vm.effects,
                        onBack = { backStack.removeLastOrNull() },
                        onSearchQueryChange = { vm.onIntent(ReferenceIntent.SearchQueryChanged(it)) },
                        onRetry = { vm.onIntent(ReferenceIntent.LoadData) },
                    )
                }
            }
            entry<ForecastDetailRoute> { route ->
                val vm = koinViewModel<ForecastDetailViewModel>(
                    key = "forecast_${route.pollenId}",
                ) { parametersOf(route.pollenId) }
                val state by vm.state.collectAsState()
                Box(
                    Modifier
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .fillMaxSize(),
                ) {
                    ForecastDetailScreen(
                        state = state,
                        effects = vm.effects,
                        onBack = { backStack.removeLastOrNull() },
                        onToggleFeeling = { vm.onIntent(ForecastDetailIntent.ToggleFeelingLine) },
                        onRetry = { vm.onIntent(ForecastDetailIntent.ReloadData) },
                    )
                }
            }
        },
    )
}
