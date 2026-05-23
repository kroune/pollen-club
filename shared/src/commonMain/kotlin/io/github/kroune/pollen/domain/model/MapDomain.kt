package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDateTime
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

data class MapPinDomain(
    val date: LocalDateTime,
    val feeling: Feeling,
    val latitude: Double,
    val longitude: Double,
    val pollenType: Int,
    val tags: Set<String>,
    val friendId: Int,
)

data class GeoPoint(val latitude: Double, val longitude: Double)

data class MapRingDomain(
    val points: List<GeoPoint>,
    val fillArgb: Int,
    val strokeArgb: Int,
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
)

fun interface TileRingQuery {
    fun query(x: Int, y: Int, zoom: Int): List<MapRingDomain>

    companion object {
        val EMPTY = TileRingQuery { _, _, _ -> emptyList() }
    }
}

class TileRingIndex private constructor(
    private val precomputed: Map<Long, List<MapRingDomain>>,
) {

    fun query(x: Int, y: Int, zoom: Int): List<MapRingDomain> {
        if (zoom <= MAX_ZOOM) {
            return precomputed[tileKey(x, y, zoom)] ?: emptyList()
        }
        val shift = zoom - MAX_ZOOM
        val parentX = x shr shift
        val parentY = y shr shift
        val parentRings = precomputed[tileKey(parentX, parentY, MAX_ZOOM)] ?: return emptyList()
        val minLng = tileToLng(x, zoom)
        val maxLng = tileToLng(x + 1, zoom)
        val maxLat = tileToLat(y, zoom)
        val minLat = tileToLat(y + 1, zoom)
        return parentRings.filter { ring ->
            ring.maxLat >= minLat && ring.minLat <= maxLat &&
                ring.maxLng >= minLng && ring.minLng <= maxLng
        }
    }

    companion object {
        private const val MAX_ZOOM = 8

        val EMPTY = TileRingIndex(emptyMap())

        fun build(rings: List<MapRingDomain>): TileRingIndex {
            if (rings.isEmpty()) return TileRingIndex(emptyMap())
            val map = HashMap<Long, MutableList<MapRingDomain>>()
            for (ring in rings) {
                for (zoom in 0..MAX_ZOOM) {
                    val tx0 = lngToTileX(ring.minLng, zoom)
                    val tx1 = lngToTileX(ring.maxLng, zoom)
                    val ty0 = latToTileY(ring.maxLat, zoom)
                    val ty1 = latToTileY(ring.minLat, zoom)
                    for (tx in tx0..tx1) {
                        for (ty in ty0..ty1) {
                            map.getOrPut(tileKey(tx, ty, zoom)) { mutableListOf() }.add(ring)
                        }
                    }
                }
            }
            return TileRingIndex(map)
        }

        fun tileKey(x: Int, y: Int, zoom: Int): Long =
            (zoom.toLong() shl 48) or (x.toLong() shl 24) or y.toLong()

        internal fun lngToTileX(lng: Double, zoom: Int): Int =
            floor((lng + 180.0) / 360.0 * (1 shl zoom)).toInt()

        internal fun latToTileY(lat: Double, zoom: Int): Int {
            val latRad = lat * PI / 180.0
            return floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl zoom)).toInt()
        }

        private fun tileToLng(x: Int, zoom: Int): Double =
            x.toDouble() / (1 shl zoom) * 360.0 - 180.0

        private fun tileToLat(y: Int, zoom: Int): Double {
            val n = PI * (1.0 - 2.0 * y.toDouble() / (1 shl zoom))
            return atan((exp(n) - exp(-n)) / 2.0) * 180.0 / PI
        }
    }
}
