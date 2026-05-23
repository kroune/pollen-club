# Work In Progress

Tracked unfinished UI sections where the ViewModel layer is already wired but the screen has not been implemented yet. Backing data flows are live; rendering is intentionally deferred. Keep this file in sync when picking these up.

## Home screen — Weather card

- Marker: `presentation/home/HomeScreen.kt` — `// TODO: Add WeatherCard section here when weather UI is implemented`.
- VM state: `HomeUiState.weather: LoadState<WeatherDomain>` is populated by `HomeViewModel.loadWeather(location)` whenever the selected location changes.
- Backing data: `WeatherRepository.getCurrentWeather(latitude, longitude)` (Open-Meteo). Errors emit `MR.strings.error_load_weather` via the existing snackbar.
- Acceptance: render a card showing temperature, day/night icon, and weather code on the Home screen; tap-through optional. Failed state should reuse `ErrorBanner`/`FullScreenError`; loading should reuse a shimmer skeleton matching the card.

## Home screen — Expanded forecast row

- Marker: `presentation/home/HomeScreen.kt` — `// TODO: Add expandable allergen row with ForecastChart`.
- VM state: `HomeUiState.expandedPollenId: Int?` and `HomeUiState.forecastTimeline: LoadState<ImmutableList<LevelDomain>>`. Toggle via `HomeIntent.ToggleAllergenExpanded(pollenId)`; chart data is loaded by `HomeViewModel.loadForecastTimeline(pollenId)`.
- Acceptance: tapping an allergen row expands an inline Vico chart (see `presentation/home/ForecastChart` in the detail screen for the canonical pattern). Collapse on second tap. Loading state shows a skeleton; failed state collapses + emits `MR.strings.error_load_forecast`.

## Medications screen — Category detail navigation

- Marker: `presentation/medications/MedicationsScreen.kt` — `.clickable { /* TODO: navigate to category detail */ }` on the category list rows.
- Currently the rows look interactive (ripple on tap) but do nothing on click.
- Acceptance: define a `CategoryDetailRoute(categoryId)` in `presentation/Routes.kt`, build a screen + VM listing cures filtered by the chosen action type using `MedicationRepository.getCureCatalog()`, and wire the click handler in `App.kt` `entry<MedicationsRoute>`.
