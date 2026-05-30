package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kroune.pollen.domain.model.DEFAULT_CENTER_LATITUDE
import io.github.kroune.pollen.domain.model.DEFAULT_CENTER_LONGITUDE
import io.github.kroune.pollen.domain.model.GeoPoint
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.TileRingQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
expect fun PlatformMapView(
    pins: PlatformMapPins,
    ringQuery: TileRingQuery,
    onPinClick: (MapPinDomain) -> Unit,
    modifier: Modifier,
    overlayBottomY: Dp = 0.dp,
    onBearingChanged: (Float) -> Unit = {},
    resetBearing: Flow<Unit> = emptyFlow(),
    initialLatitude: Double = DEFAULT_CENTER_LATITUDE,
    initialLongitude: Double = DEFAULT_CENTER_LONGITUDE,
    centerOnUser: Flow<GeoPoint> = emptyFlow(),
)
