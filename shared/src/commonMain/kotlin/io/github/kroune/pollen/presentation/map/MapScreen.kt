package io.github.kroune.pollen.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LocationAvailability
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.MapAreaSkeleton
import io.github.kroune.pollen.presentation.common.rememberLocationPermissionLauncher
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlin.math.absoluteValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(viewModel: MapViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    val requestPermission = rememberLocationPermissionLauncher { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { _ ->
        val allFailed = state.pollens is LoadState.Failed && state.pins is LoadState.Failed
        if (allFailed) {
            FullScreenError(onRetry = viewModel::loadData)
            return@Scaffold
        }

        val density = LocalDensity.current
        var overlayBottom by remember { mutableStateOf(0.dp) }
        var bearing by remember { mutableStateOf(0f) }
        var resetTrigger by remember { mutableStateOf(0) }

        Box(modifier = Modifier.fillMaxSize()) {
            when (state.pins) {
                is LoadState.Loading -> MapAreaSkeleton(modifier = Modifier.fillMaxSize())
                is LoadState.Loaded -> {
                    PlatformMapView(
                        pins = (state.pins as LoadState.Loaded).data,
                        polygons = (state.polygons as? LoadState.Loaded)?.data ?: emptyList(),
                        onPinClick = {},
                        modifier = Modifier.fillMaxSize(),
                        overlayBottomY = overlayBottom,
                        onBearingChanged = { bearing = it },
                        resetBearingTrigger = resetTrigger,
                        initialLatitude = state.centerLatitude,
                        initialLongitude = state.centerLongitude,
                        userLatitude = state.userLatitude,
                        userLongitude = state.userLongitude,
                        centerOnUserTrigger = state.centerOnUserTrigger,
                    )
                }
                is LoadState.Failed -> FullScreenError(onRetry = viewModel::loadData)
            }

            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding(),
                ) {
                    HashtagFilterRow(
                        state = state,
                        onToggle = viewModel::toggleHashtag,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                val bottomY = coordinates.positionInRoot().y + coordinates.size.height.toFloat()
                                overlayBottom = with(density) { bottomY.toDp() }
                            },
                    ) {
                        AllergenSelectorChip(
                            state = state,
                            onToggleDropdown = viewModel::toggleAllergenDropdown,
                            onDismissDropdown = viewModel::dismissAllergenDropdown,
                            onSelectAllergen = viewModel::selectAllergen,
                            modifier = Modifier.align(Alignment.Center),
                        )
                        MapCompass(
                            bearingProvider = { bearing },
                            onClick = { resetTrigger++ },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp),
                        )
                    }
                }
            }

            SeverityLegend(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
            )

            Surface(
                onClick = {
                    when (state.locationAvailability) {
                        LocationAvailability.Available -> viewModel.centerOnMyLocation()
                        LocationAvailability.PermissionDenied,
                        LocationAvailability.Unknown -> requestPermission()
                        LocationAvailability.LocationDisabled -> viewModel.showLocationDisabledSnackbar()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 14.dp)
                    .size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = stringResource(MR.strings.map_my_location),
                        tint = PollenTheme.colors.accent,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HashtagFilterRow(
    state: MapUiState,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hashtags = (state.hashtags as? LoadState.Loaded)?.data ?: return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        hashtags.forEachIndexed { index, hashtag ->
            val isActive = index in state.activeHashtagIndices
            Surface(
                onClick = { onToggle(index) },
                shape = RoundedCornerShape(12.dp),
                color = if (isActive) PollenTheme.colors.accent else Color.White,
                shadowElevation = 3.dp,
            ) {
                Text(
                    text = hashtag.value,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = if (isActive) Color.White else PollenTheme.colors.ink2,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                )
            }
        }
    }
}

@Composable
private fun AllergenSelectorChip(
    state: MapUiState,
    onToggleDropdown: () -> Unit,
    onDismissDropdown: () -> Unit,
    onSelectAllergen: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pollens = (state.pollens as? LoadState.Loaded)?.data ?: return
    if (pollens.isEmpty()) return
    val selected = pollens.getOrNull(state.selectedAllergenIndex) ?: return

    Box(modifier = modifier) {
        Surface(
            onClick = onToggleDropdown,
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 3.dp,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = selected.name,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = PollenTheme.colors.ink,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(MR.strings.map_select_allergen),
                    tint = PollenTheme.colors.ink3,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        DropdownMenu(
            expanded = state.showAllergenDropdown,
            onDismissRequest = onDismissDropdown,
        ) {
            pollens.forEachIndexed { index, pollen ->
                DropdownMenuItem(
                    text = {
                        Text(
                            pollen.name,
                            fontWeight = if (index == state.selectedAllergenIndex) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    onClick = { onSelectAllergen(index) },
                )
            }
        }
    }
}

@Composable
private fun SeverityLegend(modifier: Modifier = Modifier) {
    val colors = PollenTheme.colors

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            for (level in 5 downTo 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 1.dp),
                ) {
                    val shape = when (level) {
                        5 -> RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        1 -> RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)
                        else -> RoundedCornerShape(0.dp)
                    }
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(18.dp)
                            .background(colors.severityColor(level), shape),
                    )
                    Spacer(Modifier.width(6.dp))
                    val severityLabel = when (level) {
                        0 -> stringResource(MR.strings.severity_none_short)
                        1 -> stringResource(MR.strings.severity_low_short)
                        2 -> stringResource(MR.strings.severity_medium_short)
                        3 -> stringResource(MR.strings.severity_high_short)
                        4 -> stringResource(MR.strings.severity_very_high_short)
                        else -> stringResource(MR.strings.severity_extra_short)
                    }
                    Text(
                        text = severityLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = PollenTheme.colors.ink3,
                    )
                }
            }
        }
    }
}

@Composable
private fun MapCompass(
    bearingProvider: () -> Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier
            .size(36.dp)
            .graphicsLayer {
                val b = bearingProvider()
                alpha = if (b.absoluteValue > 1f) 1f else 0f
                rotationZ = -b
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Explore,
                contentDescription = stringResource(MR.strings.map_reset_north),
                tint = PollenTheme.colors.accent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
