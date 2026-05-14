package io.github.kroune.pollen.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.ui.geometry.Offset
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.clustering.Clustering
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.GeoPoint
import io.github.kroune.pollen.domain.model.GeoRing
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapPolygonDomain

private class PinClusterItem(
    val pin: MapPinDomain,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(pin.latitude, pin.longitude)
    override fun getTitle(): String = pin.tags.ifEmpty { "Pin" }
    override fun getSnippet(): String = pin.date
    override fun getZIndex(): Float = 0f
}

private fun feelingColor(feeling: Feeling): Color = when (feeling) {
    Feeling.GOOD -> Color(0xFF6EA85C)
    Feeling.MIDDLE -> Color(0xFFE5A50A)
    Feeling.BAD -> Color(0xFFC43D3D)
}

private fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)

private fun GeoRing.toLatLngs(): List<LatLng> = map { it.toLatLng() }

private const val DEFAULT_ZOOM = 8f
private const val USER_LOCATION_ZOOM = 14f
private const val MAX_FILL_OPACITY = 0.15f

@Composable
actual fun PlatformMapView(
    pins: List<MapPinDomain>,
    polygons: List<MapPolygonDomain>,
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
    val defaultPosition = if (pins.isNotEmpty()) {
        LatLng(pins.first().latitude, pins.first().longitude)
    } else {
        LatLng(initialLatitude, initialLongitude)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, DEFAULT_ZOOM)
    }

    val clusterItems = remember(pins) { pins.map { PinClusterItem(it) } }

    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.position.bearing }
            .collect { onBearingChanged(it) }
    }

    LaunchedEffect(resetBearingTrigger) {
        if (resetBearingTrigger > 0) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder(cameraPositionState.position)
                        .bearing(0f)
                        .build()
                ),
            )
        }
    }

    LaunchedEffect(centerOnUserTrigger) {
        if (centerOnUserTrigger > 0 && userLatitude != null && userLongitude != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLatitude, userLongitude),
                    USER_LOCATION_ZOOM,
                ),
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        contentPadding = PaddingValues(top = overlayBottomY),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false,
        ),
    ) {
        Clustering(
            items = clusterItems,
            onClusterItemClick = { item ->
                onPinClick(item.pin)
                true
            },
            clusterItemContent = { item ->
                val color = feelingColor(item.pin.feeling)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    )
                }
            },
        )

        polygons.forEach { domainPolygon ->
            val strokeColor = parseHexColor(domainPolygon.color, domainPolygon.opacity)
            val fillColor = parseHexColor(domainPolygon.fillColor, domainPolygon.fillOpacity.coerceAtMost(MAX_FILL_OPACITY))

            domainPolygon.polygons.forEach { group ->
                group.forEach { ring ->
                    val points = ring.toLatLngs()
                    if (points.size >= 3) {
                        Polygon(
                            points = points,
                            strokeColor = strokeColor,
                            fillColor = fillColor,
                            strokeWidth = 2f,
                        )
                    }
                }
            }
        }

        if (userLatitude != null && userLongitude != null) {
            MarkerComposable(
                state = MarkerState(position = LatLng(userLatitude, userLongitude)),
                anchor = Offset(0.5f, 0.5f),
                zIndex = 1f,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0x304285F4), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF4285F4), CircleShape)
                            .border(2.5.dp, Color.White, CircleShape),
                    )
                }
            }
        }
    }
}

private fun parseHexColor(hex: String, opacity: Float): Color {
    return try {
        val color = Color(android.graphics.Color.parseColor(hex))
        color.copy(alpha = opacity.coerceIn(0f, 1f))
    } catch (_: IllegalArgumentException) {
        Color.Gray.copy(alpha = opacity.coerceIn(0f, 1f))
    }
}
