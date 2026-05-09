package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapPolygonDomain

@Composable
expect fun PlatformMapView(
    pins: List<MapPinDomain>,
    polygons: List<MapPolygonDomain>,
    onPinClick: (MapPinDomain) -> Unit,
    modifier: Modifier,
    overlayBottomY: Dp = 0.dp,
    onBearingChanged: (Float) -> Unit = {},
    resetBearingTrigger: Int = 0,
)
