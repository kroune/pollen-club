package io.github.kroune.pollen.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.TileRingQuery

@Composable
actual fun PlatformMapView(
    pins: PlatformMapPins,
    ringQuery: TileRingQuery,
    onPinClick: (MapPinDomain) -> Unit,
    modifier: Modifier,
    overlayBottomY: Dp,
    onBearingChanged: (Float) -> Unit,
    resetBearingTrigger: Int,
    initialLatitude: Double,
    initialLongitude: Double,
    userLatitude: Double?,
    userLongitude: Double?,
    centerOnUserTrigger: Int,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Map (JVM stub)")
    }
}