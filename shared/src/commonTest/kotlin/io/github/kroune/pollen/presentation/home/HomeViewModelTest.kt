package io.github.kroune.pollen.presentation.home

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.DayForecastSummaryDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.domain.model.Identity
import io.github.kroune.pollen.domain.model.User
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.usecase.DayForecastSummaryUseCase
import io.github.kroune.pollen.domain.usecase.ObserveUserAllergensUseCase
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val today = LocalDate(2026, 5, 18) // Monday

    private lateinit var userRepo: FakeUserSession
    private lateinit var pollenRepo: FakePollenRepository
    private lateinit var locationRepo: FakeLocationRepository
    private lateinit var weatherRepo: FakeWeatherRepository
    private lateinit var sensitivityRepo: FakeSensitivityRepository
    private lateinit var todayProvider: FakeTodayProvider

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepo = FakeUserSession(serverId = 1L, location = null)
        pollenRepo = FakePollenRepository()
        locationRepo = FakeLocationRepository()
        weatherRepo = FakeWeatherRepository()
        sensitivityRepo = FakeSensitivityRepository()
        todayProvider = FakeTodayProvider(today)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        userSession = userRepo,
        pollenRepository = pollenRepo,
        locationRepository = locationRepo,
        weatherRepository = weatherRepo,
        observeUserAllergensUseCase = ObserveUserAllergensUseCase(pollenRepo, sensitivityRepo),
        dayForecastSummaryUseCase = DayForecastSummaryUseCase(pollenRepo),
        sensitivityRepository = sensitivityRepo,
        todayProvider = todayProvider,
    )

    private val moscow = LocationDomain(
        id = 1, name = "Moscow", description = "Moscow",
        latitude = 55.75, longitude = 37.62,
    )

    private val spb = LocationDomain(
        id = 2, name = "St. Petersburg", description = "SPb",
        latitude = 59.93, longitude = 30.31,
    )

    private val birch = PollenDomain(
        id = 10, name = "Birch", description = "Birch tree",
        maxLevel = 5, levels = emptyList(),
    )

    private val alder = PollenDomain(
        id = 20, name = "Alder", description = "Alder tree",
        maxLevel = 4, levels = emptyList(),
    )

    // value is a severity bucket on the universal 0..5 scale, not a raw concentration.
    private val birchLevel = LevelDomain(
        id = 1, date = today, pollenId = 10, locationId = 1, value = 2,
    )

    private fun weekDaySummaries(): List<DayForecastSummaryDomain> {
        val dow = today.dayOfWeek.ordinal
        val monday = today.minus(dow.toLong(), DateTimeUnit.DAY)
        return (0 until 7).map { offset ->
            val date = monday.plus(offset.toLong(), DateTimeUnit.DAY)
            DayForecastSummaryDomain(date = date, maxSeverity = 2, dominantPollenId = 10)
        }
    }

    private fun seedData() {
        locationRepo.locationsFlow.value = listOf(moscow)
        pollenRepo.pollensFlow.value = listOf(birch, alder)
        pollenRepo.emitLevels(listOf(birchLevel))
    }

    private fun TestScope.collectState(vm: HomeViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect {} }
    }

    private fun TestScope.collectEffects(vm: HomeViewModel): MutableList<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.collect { events.add(it) } }
        return events
    }

    // === Sync & Loading ===

    @Test
    fun initialState_duringSync_isRefreshing() = runTest(testDispatcher) {
        pollenRepo.syncGate = CompletableDeferred()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertTrue(vm.state.value.isRefreshing)

        pollenRepo.syncGate!!.complete(Unit)
        advanceUntilIdle()
        assertEquals(false, vm.state.value.isRefreshing)
    }

    @Test
    fun afterSync_withData_showsLoadedState() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        val state = vm.state.value
        assertIs<LoadState.Loaded<*>>(state.pollens)
        assertEquals(2, state.pollens.dataOrNull!!.size)
        assertEquals(HomeLocationUi(moscow.id, moscow.name), state.selectedLocation)
        assertIs<LoadState.Loaded<*>>(state.locations)
    }

    @Test
    fun afterSync_dayForecastsAreLoaded() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertIs<LoadState.Loaded<*>>(vm.state.value.dayForecasts)
        assertEquals(7, vm.state.value.dayForecasts.dataOrNull!!.size)
    }

    @Test
    fun afterSyncCompletes_emptyDataShowsLoaded() = runTest(testDispatcher) {
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(false, state.isRefreshing)
        assertIs<LoadState.Loaded<*>>(state.pollens)
        assertTrue(state.pollens.dataOrNull!!.isEmpty())
    }

    @Test
    fun isRefreshing_falseAfterSyncCompletes() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertEquals(false, vm.state.value.isRefreshing)
    }

    @Test
    fun loadDataIntent_triggersRefreshCycle() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        assertEquals(false, vm.state.value.isRefreshing)

        pollenRepo.syncGate = CompletableDeferred()
        vm.onIntent(HomeIntent.LoadData)
        advanceUntilIdle()
        assertEquals(true, vm.state.value.isRefreshing)

        pollenRepo.syncGate!!.complete(Unit)
        advanceUntilIdle()
        assertEquals(false, vm.state.value.isRefreshing)
    }

    // === Sync error paths ===

    @Test
    fun syncFailure_sendsErrorEffect() = runTest(testDispatcher) {
        seedData()
        pollenRepo.syncLevelsError = RuntimeException("network")
        val vm = createViewModel()
        collectState(vm)
        val events = collectEffects(vm)
        advanceUntilIdle()

        assertTrue(events.any { it is UiEvent.ShowError })
    }

    @Test
    fun syncFailure_stillLoadsAvailableData() = runTest(testDispatcher) {
        seedData()
        pollenRepo.syncForecastsError = RuntimeException("timeout")
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertIs<LoadState.Loaded<*>>(vm.state.value.pollens)
        assertEquals(2, vm.state.value.pollens.dataOrNull!!.size)
        assertEquals(false, vm.state.value.isRefreshing)
    }

    // === Weather ===

    @Test
    fun weatherLoaded_afterSync() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        val weather = vm.state.value.weather
        assertIs<LoadState.Loaded<*>>(weather)
        assertEquals(22.0, (weather.dataOrNull as HomeWeatherUi).temperature)
    }

    @Test
    fun weatherError_showsFailed() = runTest(testDispatcher) {
        seedData()
        weatherRepo.result = ApiResult.Error("fail")
        val vm = createViewModel()
        collectState(vm)
        val events = collectEffects(vm)
        advanceUntilIdle()

        assertIs<LoadState.Failed>(vm.state.value.weather)
        assertTrue(events.any { it is UiEvent.ShowError })
    }

    // === Location selection ===

    @Test
    fun emptyLocations_selectedLocationIsNull() = runTest(testDispatcher) {
        pollenRepo.pollensFlow.value = listOf(birch)
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertNull(vm.state.value.selectedLocation)
    }

    @Test
    fun userPreferredLocation_isSelected() = runTest(testDispatcher) {
        locationRepo.locationsFlow.value = listOf(moscow, spb)
        pollenRepo.pollensFlow.value = listOf(birch)
        userRepo.userFlow.value = User(identity = Identity.Registered(1), location = 2)
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertEquals(HomeLocationUi(spb.id, spb.name), vm.state.value.selectedLocation)
    }

    @Test
    fun selectLocationIntent_updatesSelectedLocation() = runTest(testDispatcher) {
        seedData()
        locationRepo.locationsFlow.value = listOf(moscow, spb)
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        assertEquals(HomeLocationUi(moscow.id, moscow.name), vm.state.value.selectedLocation)

        vm.onIntent(HomeIntent.SelectLocation(spb.id))
        advanceUntilIdle()

        assertEquals(HomeLocationUi(spb.id, spb.name), vm.state.value.selectedLocation)
    }

    @Test
    fun showAndDismissLocationPicker_togglesFlag() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        assertEquals(false, vm.state.value.showLocationPicker)

        vm.onIntent(HomeIntent.ShowLocationPicker)
        advanceUntilIdle()
        assertEquals(true, vm.state.value.showLocationPicker)

        vm.onIntent(HomeIntent.DismissLocationPicker)
        advanceUntilIdle()
        assertEquals(false, vm.state.value.showLocationPicker)
    }

    // === Allergens & Sensitivity ===

    @Test
    fun noSensitivities_allPollensInOtherAllergens() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertTrue(vm.state.value.userAllergens.isEmpty())
        assertEquals(2, vm.state.value.otherAllergens.size)
    }

    @Test
    fun withSensitivities_allergensPartitioned() = runTest(testDispatcher) {
        seedData()
        sensitivityRepo.emit(listOf(AllergenSensitivityDomain(birch.id, SensitivityLevel.MODERATE)))
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertEquals(1, vm.state.value.userAllergens.size)
        assertEquals(birch.id, vm.state.value.userAllergens[0].pollen.id)
        assertEquals(1, vm.state.value.otherAllergens.size)
        assertEquals(alder.id, vm.state.value.otherAllergens[0].id)
    }

    @Test
    fun allergenSeverity_reflectsLevelData() = runTest(testDispatcher) {
        seedData()
        sensitivityRepo.emit(listOf(AllergenSensitivityDomain(birch.id, SensitivityLevel.MODERATE)))
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        val allergen = vm.state.value.userAllergens.first()
        assertEquals(birch.id, allergen.pollen.id)
        assertEquals(2, allergen.level)
    }

    @Test
    fun addAllergenIntent_movesFromOtherToUser() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.userAllergens.size)
        assertEquals(2, vm.state.value.otherAllergens.size)

        vm.onIntent(HomeIntent.AddAllergen(birch.id))
        advanceUntilIdle()

        assertEquals(1, vm.state.value.userAllergens.size)
        assertEquals(birch.id, vm.state.value.userAllergens[0].pollen.id)
        assertEquals(1, vm.state.value.otherAllergens.size)
    }

    @Test
    fun addAllergenIntent_setsLightSensitivity() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.AddAllergen(alder.id))
        advanceUntilIdle()

        val sensitivities = sensitivityRepo.getAll()
        assertEquals(1, sensitivities.size)
        assertEquals(alder.id, sensitivities[0].pollenId)
        assertEquals(SensitivityLevel.LIGHT, sensitivities[0].level)
    }

    @Test
    fun addAllergenError_sendsEffect() = runTest(testDispatcher) {
        seedData()
        sensitivityRepo.setSensitivityError = RuntimeException("db error")
        val vm = createViewModel()
        collectState(vm)
        val events = collectEffects(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.AddAllergen(birch.id))
        advanceUntilIdle()

        assertTrue(events.any { it is UiEvent.ShowError })
    }

    // === Personal index ===

    @Test
    fun noSensitivities_personalIndexIsNull() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        assertIs<LoadState.Loaded<*>>(vm.state.value.personalIndex)
        assertNull(vm.state.value.personalIndex.dataOrNull)
    }

    @Test
    fun withSensitivities_personalIndexIsComputed() = runTest(testDispatcher) {
        seedData()
        sensitivityRepo.emit(listOf(AllergenSensitivityDomain(birch.id, SensitivityLevel.MODERATE)))
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        val pi = vm.state.value.personalIndex
        assertIs<LoadState.Loaded<*>>(pi)
        assertNotNull(pi.dataOrNull)
    }

    // === Day selection ===

    @Test
    fun selectDayIntent_updatesActiveDayIndex() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.SelectDay(3))
        advanceUntilIdle()

        assertEquals(3, vm.state.value.activeDayIndex)
    }

    @Test
    fun selectDayIntent_outOfBounds_noOp() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        val indexBefore = vm.state.value.activeDayIndex

        vm.onIntent(HomeIntent.SelectDay(99))
        advanceUntilIdle()

        assertEquals(indexBefore, vm.state.value.activeDayIndex)
    }

    @Test
    fun selectDayIntent_loadsLevelsForSelectedDate() = runTest(testDispatcher) {
        seedData()
        sensitivityRepo.emit(listOf(AllergenSensitivityDomain(birch.id, SensitivityLevel.MODERATE)))
        val summaries = weekDaySummaries()
        val tuesdayDate = summaries[1].date
        val tuesdayLevel = LevelDomain(id = 99, date = tuesdayDate, pollenId = 10, locationId = 1, value = 4)
        pollenRepo.emitLevels(listOf(birchLevel, tuesdayLevel))
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.SelectDay(1))
        advanceUntilIdle()

        val allergen = vm.state.value.userAllergens.first()
        // The level value IS the severity bucket on the 0..5 scale.
        assertEquals(4, allergen.level)
    }

    @Test
    fun selectDayIntent_emptyLevelsAndForecasts_stillUpdatesIndex() = runTest(testDispatcher) {
        seedData()
        val summaries = weekDaySummaries()
        val tuesdayDate = summaries[1].date
        pollenRepo.levelsForDate[1 to tuesdayDate] = emptyList()
        pollenRepo.forecastsForDate[1 to tuesdayDate] = emptyList()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.SelectDay(1))
        advanceUntilIdle()

        assertEquals(1, vm.state.value.activeDayIndex)
    }

    // === Week navigation ===

    @Test
    fun shiftWeekIntent_updatesWeekOffset() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.weekOffset)

        vm.onIntent(HomeIntent.ShiftWeek(1))
        advanceUntilIdle()

        assertEquals(1, vm.state.value.weekOffset)
        assertEquals(0, vm.state.value.activeDayIndex)
    }

    @Test
    fun shiftWeekIntent_negativeDelta_updatesOffset() = runTest(testDispatcher) {
        seedData()
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.ShiftWeek(-1))
        advanceUntilIdle()

        assertEquals(-1, vm.state.value.weekOffset)
    }

    // === Expanded allergen & forecast timeline ===

    @Test
    fun toggleAllergenExpandedIntent_setsExpandedId() = runTest(testDispatcher) {
        seedData()
        pollenRepo.forecastTimelines[1 to birch.id] = listOf(birchLevel)
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()
        assertNull(vm.state.value.expandedPollenId)

        vm.onIntent(HomeIntent.ToggleAllergenExpanded(birch.id))
        advanceUntilIdle()

        assertEquals(birch.id, vm.state.value.expandedPollenId)
        assertIs<LoadState.Loaded<*>>(vm.state.value.forecastTimeline)
    }

    @Test
    fun toggleAllergenExpandedIntent_collapses() = runTest(testDispatcher) {
        seedData()
        pollenRepo.forecastTimelines[1 to birch.id] = listOf(birchLevel)
        val vm = createViewModel()
        collectState(vm)
        advanceUntilIdle()

        vm.onIntent(HomeIntent.ToggleAllergenExpanded(birch.id))
        advanceUntilIdle()
        assertEquals(birch.id, vm.state.value.expandedPollenId)

        vm.onIntent(HomeIntent.ToggleAllergenExpanded(birch.id))
        advanceUntilIdle()
        assertNull(vm.state.value.expandedPollenId)
    }
}
