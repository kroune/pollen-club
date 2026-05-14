package io.github.kroune.pollen.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.DayStripSkeleton
import io.github.kroune.pollen.presentation.common.ErrorBanner
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.LocationHeaderSkeleton
import io.github.kroune.pollen.presentation.common.PersonalIndexCardSkeleton
import io.github.kroune.pollen.presentation.common.PollenListSkeleton
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToForecast: (pollenId: Int) -> Unit = {},
    onNavigateToAllergenSettings: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    if (state.showLocationPicker) {
        val locations = state.locations
        if (locations is LoadState.Loaded) {
            LocationPickerDialog(
                locations = locations.data,
                selectedLocation = state.selectedLocation,
                onSelect = viewModel::selectLocation,
                onDismiss = viewModel::dismissLocationPicker,
            )
        }
    }

    val allFailed = state.pollens is LoadState.Failed &&
        state.locations is LoadState.Failed

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        if (allFailed) {
            FullScreenError(onRetry = viewModel::loadData)
            return@Scaffold
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::loadData,
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Location header
                item {
                    when (state.locations) {
                        is LoadState.Loading -> LocationHeaderSkeleton(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                        is LoadState.Loaded, is LoadState.Failed -> {
                            LocationRow(
                                locationName = state.selectedLocation?.name ?: "—",
                                onLocationClick = viewModel::showLocationPicker,
                                onSettingsClick = onNavigateToSettings,
                            )
                        }
                    }
                }

                // TODO: Add WeatherCard section here when weather UI is implemented

                // Day strip
                item {
                    when (val forecasts = state.dayForecasts) {
                        is LoadState.Loading -> {
                            DayStripSkeleton()
                        }
                        is LoadState.Loaded -> {
                            if (forecasts.data.isNotEmpty()) {
                                DayStrip(
                                    days = forecasts.data,
                                    activeDayIndex = state.activeDayIndex,
                                    weekLabel = state.weekLabel.localized(),
                                    onDaySelected = viewModel::selectDay,
                                    onPreviousWeek = { viewModel.shiftWeek(-1) },
                                    onNextWeek = { viewModel.shiftWeek(1) },
                                )
                            }
                        }
                        is LoadState.Failed -> {
                            ErrorBanner(onRetry = viewModel::loadData)
                        }
                    }
                }

                // Personal index card
                item {
                    when (val index = state.personalIndex) {
                        is LoadState.Loading -> {
                            PersonalIndexCardSkeleton(
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                        is LoadState.Loaded -> {
                            val data = index.data
                            if (data != null) {
                                PersonalIndexCard(
                                    score = data.score,
                                    severityLevel = data.severityLevel,
                                    label = data.label.localized(),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            } else {
                                ConfigureAllergensCard(
                                    onClick = onNavigateToAllergenSettings,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }
                        is LoadState.Failed -> {
                            ErrorBanner(onRetry = viewModel::loadData)
                        }
                    }
                }

                // Pollen list
                when (val pollens = state.pollens) {
                    is LoadState.Loading -> {
                        item { PollenListSkeleton(modifier = Modifier.padding(horizontal = 16.dp)) }
                    }
                    is LoadState.Loaded -> {
                        if (pollens.data.isEmpty()) {
                            item {
                                Box(
                                    Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(stringResource(MR.strings.home_no_pollen_data))
                                }
                            }
                        } else {
                            if (state.userAllergens.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = stringResource(MR.strings.home_your_allergens).uppercase(),
                                        actionLabel = stringResource(MR.strings.home_configure),
                                        onAction = onNavigateToAllergenSettings,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                                    )
                                }
                                item {
                                    AllergenListCard(
                                        allergens = state.userAllergens,
                                        onAllergenClick = onNavigateToForecast,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                            }

                            // TODO: Add expandable allergen row with ForecastChart

                            if (state.otherAllergens.isNotEmpty()) {
                                item {
                                    OtherAllergensSection(
                                        allergens = state.otherAllergens,
                                        onAllergenAdd = viewModel::addAllergen,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                            }
                        }
                    }
                    is LoadState.Failed -> {
                        item { ErrorBanner(onRetry = viewModel::loadData) }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// --- Components matching the polished design ---

@Composable
fun LocationRow(
    locationName: String,
    onLocationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onLocationClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = PollenTheme.colors.ink3,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = locationName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink2,
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(MR.strings.home_change_city),
                tint = PollenTheme.colors.ink3,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Icon(
            Icons.Default.Settings,
            contentDescription = stringResource(MR.strings.settings_title),
            tint = PollenTheme.colors.ink3,
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onSettingsClick),
        )
    }
}

@Composable
fun DayStrip(
    days: ImmutableList<HomeDayForecastUi>,
    activeDayIndex: Int,
    weekLabel: String,
    onDaySelected: (Int) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPreviousWeek, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(MR.strings.home_prev_week),
                    tint = PollenTheme.colors.ink3,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = weekLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink2,
            )
            IconButton(onClick = onNextWeek, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(MR.strings.home_next_week),
                    tint = PollenTheme.colors.ink3,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val shape = RoundedCornerShape(10.dp)
            val borderWidth = 1.5.dp
            days.forEachIndexed { i, day ->
                val isActive = i == activeDayIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .clickable { onDaySelected(i) }
                        .background(
                            if (isActive) PollenTheme.colors.card else Color.Transparent,
                            shape,
                        )
                        .border(
                            BorderStroke(
                                borderWidth,
                                if (isActive) PollenTheme.colors.accent else Color.Transparent,
                            ),
                            shape,
                        )
                        .padding(vertical = 7.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DayStripContent(day, isActive)
                }
            }
        }
    }
}

@Composable
private fun DayStripContent(day: HomeDayForecastUi, isActive: Boolean) {
    Text(
        text = day.dayOfWeek.localized().uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = if (isActive) PollenTheme.colors.accent2 else PollenTheme.colors.ink3,
        textAlign = TextAlign.Center,
    )
    Text(
        text = "${day.dayOfMonth}",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
        color = if (isActive) PollenTheme.colors.ink else PollenTheme.colors.ink2,
        modifier = Modifier.padding(top = 3.dp),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(5.dp))
    Box(
        modifier = Modifier
            .size(7.dp)
            .background(
                PollenTheme.colors.severityColor(day.severity),
                CircleShape,
            ),
    )
}

@Composable
fun PersonalIndexCard(
    score: String,
    severityLevel: Int,
    label: String,
    maxLevel: Int = 5,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = score,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.severityColor(severityLevel),
                modifier = Modifier.padding(end = 10.dp),
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                for (i in 1..maxLevel) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                if (i <= severityLevel) PollenTheme.colors.severityColor(i)
                                else PollenTheme.colors.line2,
                                RoundedCornerShape(3.dp),
                            ),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink2,
            )
        }
    }
}

@Composable
fun ConfigureAllergensCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(MR.strings.home_configure_allergens_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = PollenTheme.colors.ink2,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(MR.strings.home_configure_allergens_action),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.accent2,
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = PollenTheme.colors.ink3,
        )
        Spacer(Modifier.weight(1f))
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.accent2,
                modifier = if (onAction != null) Modifier.clickable(onClick = onAction) else Modifier,
            )
        }
    }
}

@Composable
fun SeverityDotsRow(
    level: Int,
    maxDots: Int = 5,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (i in 1..maxDots) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (i <= level) PollenTheme.colors.severityColor(level)
                        else PollenTheme.colors.line2,
                        CircleShape,
                    ),
            )
        }
    }
}

@Composable
fun AllergenListCard(
    allergens: ImmutableList<AllergenRowData>,
    onAllergenClick: (pollenId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            allergens.forEachIndexed { index, allergen ->
                if (index > 0) {
                    HorizontalDivider(color = PollenTheme.colors.line2)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAllergenClick(allergen.pollen.id) }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = allergen.pollen.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    SeverityDotsRow(level = allergen.severity)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = PollenTheme.colors.ink3,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OtherAllergensSection(
    allergens: ImmutableList<PollenDomain>,
    onAllergenAdd: (pollenId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(MR.strings.home_other).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = PollenTheme.colors.ink3,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            allergens.forEach { pollen ->
                AllergenPill(
                    name = pollen.name,
                    onClick = { onAllergenAdd(pollen.id) },
                )
            }
        }
    }
}

@Composable
fun AllergenPill(
    name: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(PollenTheme.colors.paper2, RoundedCornerShape(100.dp))
            .border(1.dp, PollenTheme.colors.line2, RoundedCornerShape(100.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(PollenTheme.colors.severity0, CircleShape),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.ink2,
        )
    }
}

@Composable
fun WeatherCard(weather: io.github.kroune.pollen.domain.model.WeatherDomain) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = PollenTheme.colors.card,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${weather.temperature.toInt()}°",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(20.dp))
            Text(
                text = stringResource(io.github.kroune.pollen.util.weatherCodeToResource(weather.weatherCode)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
