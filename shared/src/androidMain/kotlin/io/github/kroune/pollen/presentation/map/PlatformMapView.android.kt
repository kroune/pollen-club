package io.github.kroune.pollen.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color.TRANSPARENT
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.LruCache
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.rememberCameraPositionState
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.GeoPoint
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapRingDomain
import io.github.kroune.pollen.domain.model.TileRingIndex
import io.github.kroune.pollen.domain.model.TileRingQuery
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sin

private fun feelingColorInt(feeling: Feeling): Int = when (feeling) {
    Feeling.GOOD -> 0xFF6EA85C.toInt()
    Feeling.MIDDLE -> 0xFFE5A50A.toInt()
    Feeling.BAD -> 0xFFC43D3D.toInt()
}

private const val DEFAULT_ZOOM = 8f
private const val TILE_SIZE = 256
private const val TILE_CACHE_COUNT = 256
private const val STREAM_CAPACITY = 16 * 1024

// ── Mercator projection ─────────────────────────────────────────────

private fun lngToPixelX(lng: Double, scale: Int): Double =
    (lng + 180.0) / 360.0 * scale

private fun latToPixelY(lat: Double, scale: Int): Double {
    val sinLat = sin(lat * PI / 180.0)
    return (0.5 - ln((1 + sinLat) / (1 - sinLat)) / (4 * PI)) * scale
}

private fun pixelXToLng(px: Int, scale: Int): Double =
    px.toDouble() / scale * 360.0 - 180.0

private fun pixelYToLat(py: Int, scale: Int): Double {
    val mercY = PI * (1.0 - 2.0 * py / scale)
    return Math.toDegrees(atan((exp(mercY) - exp(-mercY)) / 2.0))
}

// ── Tile bounds in geo coordinates ──────────────────────────────────

private class TileBounds(x: Int, y: Int, zoom: Int) {
    val originX = x * TILE_SIZE
    val originY = y * TILE_SIZE
    val scale = TILE_SIZE * (1 shl zoom)
    val minLng = pixelXToLng(originX, scale)
    val maxLng = pixelXToLng(originX + TILE_SIZE, scale)
    val minLat = pixelYToLat(originY + TILE_SIZE, scale)
    val maxLat = pixelYToLat(originY, scale)
}

// ── ThreadLocal helper ──────────────────────────────────────────────

private class NonNullThreadLocal<T : Any>(private val init: () -> T) {
    private val delegate = object : ThreadLocal<T>() {
        override fun initialValue(): T = init()
    }

    fun get(): T = delegate.get()!!
}

private fun <T : Any> threadLocal(init: () -> T): NonNullThreadLocal<T> = NonNullThreadLocal(init)

// ── Tile provider ───────────────────────────────────────────────────

private class PollenTileProvider(
    private val ringQuery: TileRingQuery,
    private val renderSize: Int,
) : TileProvider {

    private val cache = LruCache<Long, ByteArray>(TILE_CACHE_COUNT)
    private val densityScale = renderSize.toFloat() / TILE_SIZE

    private val bitmapLocal = threadLocal { createBitmap(renderSize, renderSize) }
    private val canvasLocal = threadLocal { Canvas() }
    private val pathLocal = threadLocal { Path() }
    private val fillPaintLocal =
        threadLocal { Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL } }
    private val strokePaintLocal = threadLocal {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 2f * densityScale
        }
    }
    private val streamLocal = threadLocal { ByteArrayOutputStream(STREAM_CAPACITY) }

    override fun getTile(x: Int, y: Int, zoom: Int): Tile {
        val key = TileRingIndex.tileKey(x, y, zoom)
        cache.get(key)?.let { return Tile(renderSize, renderSize, it) }

        val rings = ringQuery.query(x, y, zoom)
        if (rings.isEmpty()) return TileProvider.NO_TILE

        val bounds = TileBounds(x, y, zoom)
        val data = renderTile(rings, bounds)
        cache.put(key, data)
        return Tile(renderSize, renderSize, data)
    }

    private fun renderTile(rings: List<MapRingDomain>, bounds: TileBounds): ByteArray {
        val bitmap = bitmapLocal.get().apply { eraseColor(TRANSPARENT) }
        val canvas = canvasLocal.get().apply { setBitmap(bitmap) }
        val path = pathLocal.get()
        val fillPaint = fillPaintLocal.get()
        val strokePaint = strokePaintLocal.get()

        for (ring in rings) {
            buildRingPath(path, ring, bounds)
            fillPaint.color = ring.fillArgb
            canvas.drawPath(path, fillPaint)
            strokePaint.color = ring.strokeArgb
            canvas.drawPath(path, strokePaint)
        }

        val stream = streamLocal.get().apply { reset() }
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    private fun buildRingPath(path: Path, ring: MapRingDomain, bounds: TileBounds) {
        path.reset()
        val points = ring.points
        val first = points[0]
        path.moveTo(
            ((lngToPixelX(
                first.longitude,
                bounds.scale,
            ) - bounds.originX) * densityScale).toFloat(),
            ((latToPixelY(first.latitude, bounds.scale) - bounds.originY) * densityScale).toFloat(),
        )
        for (i in 1 until points.size) {
            val p = points[i]
            path.lineTo(
                ((lngToPixelX(
                    p.longitude,
                    bounds.scale,
                ) - bounds.originX) * densityScale).toFloat(),
                ((latToPixelY(p.latitude, bounds.scale) - bounds.originY) * densityScale).toFloat(),
            )
        }
        path.close()
    }

}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
actual fun PlatformMapView(
    pins: PlatformMapPins,
    ringQuery: TileRingQuery,
    onPinClick: (MapPinDomain) -> Unit,
    modifier: Modifier,
    overlayBottomY: Dp,
    onBearingChanged: (Float) -> Unit,
    resetBearing: Flow<Unit>,
    initialLatitude: Double,
    initialLongitude: Double,
    centerOnUser: Flow<GeoPoint>,
) {
    val defaultPosition = LatLng(initialLatitude, initialLongitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, DEFAULT_ZOOM)
    }

    val density = LocalDensity.current.density
    val tileProvider = remember(ringQuery, density) {
        val renderSize = (TILE_SIZE * density).roundToInt()
        PollenTileProvider(ringQuery, renderSize)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.position.bearing }
            .collect { onBearingChanged(it) }
    }

    LaunchedEffect(resetBearing) {
        resetBearing.collect {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder(cameraPositionState.position)
                        .bearing(0f)
                        .build(),
                ),
            )
        }
    }

    LaunchedEffect(centerOnUser) {
        centerOnUser.collect { target ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(target.latitude, target.longitude),
                    cameraPositionState.position.zoom.coerceAtLeast(DEFAULT_ZOOM),
                ),
            )
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val mapWidthPx = constraints.maxWidth
        val mapHeightPx = constraints.maxHeight

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(top = overlayBottomY),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
            ),
        ) {
            MapClustering(
                pins = pins,
                onPinClick = onPinClick,
                mapWidthPx = mapWidthPx,
                mapHeightPx = mapHeightPx,
            )

            MapEffect(tileProvider) { map ->
                val overlay = map.addTileOverlay(
                    TileOverlayOptions()
                        .tileProvider(tileProvider)
                        .transparency(0f)
                        .fadeIn(false),
                )
                try {
                    awaitCancellation()
                } finally {
                    overlay?.remove()
                }
            }
        }
    }
}

// ── Canvas-based cluster renderer (no Compose views) ───────────────

private const val INNER_CIRCLE_OVERLAY = 0x4DFFFFFF // 30% white
private const val CLUSTER_RING_OVERLAY = 0x33FFFFFF // 20% white

private class CanvasClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<PinClusterItem>,
) : DefaultClusterRenderer<PinClusterItem>(context, map, clusterManager) {

    private val density = context.resources.displayMetrics.density
    private val pinSizePx = (28 * density).roundToInt()
    private val innerSizePx = (20 * density).roundToInt()
    private val clusterSizePx = (40 * density).roundToInt()

    private val pinDescriptors = HashMap<Feeling, BitmapDescriptor>(3)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        textSize = 14 * density
    }

    private fun pinDescriptor(feeling: Feeling): BitmapDescriptor =
        pinDescriptors.getOrPut(feeling) {
            val bmp = createBitmap(pinSizePx, pinSizePx)
            val c = Canvas(bmp)
            val cx = pinSizePx / 2f
            val cy = pinSizePx / 2f
            fillPaint.color = feelingColorInt(feeling)
            c.drawCircle(cx, cy, pinSizePx / 2f, fillPaint)
            fillPaint.color = INNER_CIRCLE_OVERLAY
            c.drawCircle(cx, cy, innerSizePx / 2f, fillPaint)
            BitmapDescriptorFactory.fromBitmap(bmp)
        }

    private fun dominantFeeling(cluster: Cluster<PinClusterItem>): Feeling {
        var bad = 0;
        var mid = 0;
        var good = 0
        for (item in cluster.items) {
            when (item.pin.feeling) {
                Feeling.BAD -> bad++
                Feeling.MIDDLE -> mid++
                Feeling.GOOD -> good++
            }
        }
        return when {
            bad >= mid && bad >= good -> Feeling.BAD
            mid >= good -> Feeling.MIDDLE
            else -> Feeling.GOOD
        }
    }

    private fun clusterDescriptor(cluster: Cluster<PinClusterItem>): BitmapDescriptor {
        val feeling = dominantFeeling(cluster)
        val text = cluster.size.toString()

        val bmp = createBitmap(clusterSizePx, clusterSizePx)
        val c = Canvas(bmp)
        val cx = clusterSizePx / 2f
        val cy = clusterSizePx / 2f
        fillPaint.color = feelingColorInt(feeling)
        c.drawCircle(cx, cy, clusterSizePx / 2f, fillPaint)
        fillPaint.color = CLUSTER_RING_OVERLAY
        c.drawCircle(cx, cy, clusterSizePx / 2f - 3 * density, fillPaint)
        val textY = cy - (textPaint.descent() + textPaint.ascent()) / 2f
        c.drawText(text, cx, textY, textPaint)

        return BitmapDescriptorFactory.fromBitmap(bmp)
    }

    override fun onBeforeClusterItemRendered(item: PinClusterItem, markerOptions: MarkerOptions) {
        markerOptions.icon(pinDescriptor(item.pin.feeling))
        markerOptions.anchor(0.5f, 0.5f)
    }

    override fun onClusterItemUpdated(
        item: PinClusterItem,
        marker: com.google.android.gms.maps.model.Marker,
    ) {
        marker.setIcon(pinDescriptor(item.pin.feeling))
        marker.setAnchor(0.5f, 0.5f)
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<PinClusterItem>,
        markerOptions: MarkerOptions,
    ) {
        markerOptions.icon(clusterDescriptor(cluster))
        markerOptions.anchor(0.5f, 0.5f)
    }

    override fun onClusterUpdated(
        cluster: Cluster<PinClusterItem>,
        marker: com.google.android.gms.maps.model.Marker,
    ) {
        marker.setIcon(clusterDescriptor(cluster))
        marker.setAnchor(0.5f, 0.5f)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<PinClusterItem>): Boolean =
        cluster.size > 1
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
@com.google.maps.android.compose.GoogleMapComposable
private fun MapClustering(
    pins: PlatformMapPins,
    onPinClick: (MapPinDomain) -> Unit,
    mapWidthPx: Int,
    mapHeightPx: Int,
) {
    val context = LocalContext.current
    val clusterManager = rememberClusterManager<PinClusterItem>()

    clusterManager ?: return

    MapEffect(context, mapWidthPx, mapHeightPx) { map ->
        if (mapWidthPx > 0 && mapHeightPx > 0) {
            clusterManager.setAlgorithm(
                NonHierarchicalViewBasedAlgorithm(mapWidthPx, mapHeightPx),
            )
        }
        clusterManager.setAnimation(false)
        clusterManager.renderer = CanvasClusterRenderer(context, map, clusterManager)
    }

    androidx.compose.runtime.SideEffect {
        clusterManager.setOnClusterItemClickListener { item ->
            onPinClick(item.pin)
            true
        }
    }

    if (clusterManager.renderer is CanvasClusterRenderer) {
        Clustering(
            items = pins.items,
            clusterManager = clusterManager,
        )
    }
}
