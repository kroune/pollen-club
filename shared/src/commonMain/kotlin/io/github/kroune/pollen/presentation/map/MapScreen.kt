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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationAvailability
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.TileRingQuery
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.MapAreaSkeleton
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.rememberLocationPermissionLauncher
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.math.absoluteValue

@Composable
fun MapScreen(
    state: MapUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onIntent: (MapIntent) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState, onRetry = { onIntent(MapIntent.LoadData) })

    val requestPermission = rememberLocationPermissionLauncher { granted ->
        onIntent(MapIntent.LocationPermissionResult(granted))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { _ ->
        val density = LocalDensity.current
        var overlayBottom by remember { mutableStateOf(0.dp) }
        var bearing by remember { mutableStateOf(0f) }
        var resetTrigger by remember { mutableStateOf(0) }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val pins = state.pins) {
                is LoadState.Loading -> MapAreaSkeleton(modifier = Modifier.fillMaxSize())
                is LoadState.Failed -> FullScreenError(onRetry = { onIntent(MapIntent.LoadData) })
                is LoadState.Loaded -> {
                    val ringQuery = when (val rq = state.ringQuery) {
                        is LoadState.Loaded -> rq.data
                        else -> TileRingQuery.EMPTY
                    }
                    PlatformMapView(
                        pins = pins.data,
                        ringQuery = ringQuery,
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
                        onToggle = { onIntent(MapIntent.ToggleHashtag(it)) },
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
                            onToggleDropdown = { onIntent(MapIntent.ToggleAllergenDropdown) },
                            onDismissDropdown = { onIntent(MapIntent.DismissAllergenDropdown) },
                            onSelectAllergen = { onIntent(MapIntent.SelectAllergen(it)) },
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
                    onIntent(MapIntent.CenterOnMyLocationClicked)
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
    val hashtags = (state.hashtags.dataOrNull) ?: return

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
                        fontSize = 13.sp,
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
    val pollens = (state.pollens.dataOrNull) ?: return
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
                        fontSize = 13.sp,
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

// region Previews

@Preview
@Composable
private fun PreviewSeverityLegend() {
    PollenTheme {
        Box(Modifier.padding(16.dp)) {
            SeverityLegend()
        }
    }
}

@Preview
@Composable
private fun PreviewMapCompass() {
    PollenTheme {
        Box(Modifier.padding(16.dp)) {
            MapCompass(
                bearingProvider = { 45f },
                onClick = {},
            )
        }
    }
}

// endregion

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

// ── Previews ────────────────────────────────────────────────────────

private val previewHashtags = persistentListOf(
    HashTagDomain(id = "1", value = "#берёза", name = "Birch"),
    HashTagDomain(id = "2", value = "#ольха", name = "Alder"),
    HashTagDomain(id = "3", value = "#злаки", name = "Grasses"),
)

private val previewPollens = persistentListOf(
    PollenDomain(id = 1, name = "Берёза", description = "", maxLevel = 4, levels = emptyList()),
    PollenDomain(id = 2, name = "Ольха", description = "", maxLevel = 4, levels = emptyList()),
    PollenDomain(id = 3, name = "Злаки", description = "", maxLevel = 3, levels = emptyList()),
)

@Preview
@Composable
private fun PreviewHashtagFilterRow() {
    PollenTheme {
        Box(Modifier.background(Color.White)) {
            HashtagFilterRow(
                state = MapUiState(
                    hashtags = LoadState.Loaded(previewHashtags),
                    activeHashtagIndices = persistentSetOf(1),
                ),
                onToggle = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAllergenSelectorChip() {
    PollenTheme {
        Box(Modifier.background(Color.White).padding(16.dp)) {
            AllergenSelectorChip(
                state = MapUiState(
                    pollens = LoadState.Loaded(previewPollens),
                    selectedAllergenIndex = 0,
                ),
                onToggleDropdown = {},
                onDismissDropdown = {},
                onSelectAllergen = {},
            )
        }
    }
}


