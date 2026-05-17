package io.github.kroune.pollen.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TileRingIndexTest {

    private fun ring(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double,
    ) = MapRingDomain(
        points = listOf(
            GeoPoint(minLat, minLng),
            GeoPoint(maxLat, minLng),
            GeoPoint(maxLat, maxLng),
            GeoPoint(minLat, maxLng),
        ),
        fillArgb = 0x40FF0000,
        strokeArgb = 0xFFFF0000.toInt(),
        minLat = minLat,
        maxLat = maxLat,
        minLng = minLng,
        maxLng = maxLng,
    )

    private fun lngToTileX(lng: Double, zoom: Int): Int =
        TileRingIndex.lngToTileX(lng, zoom)

    private fun latToTileY(lat: Double, zoom: Int): Int =
        TileRingIndex.latToTileY(lat, zoom)

    // ── Empty / trivial ──────────────────────────────────────────────────

    @Test
    fun emptyConstantReturnsEmptyForAnyTile() {
        val index = TileRingIndex.EMPTY
        assertTrue(index.query(0, 0, 0).isEmpty())
        assertTrue(index.query(128, 64, 8).isEmpty())
        assertTrue(index.query(500, 300, 12).isEmpty())
    }

    @Test
    fun buildWithEmptyRingsReturnsEmptyIndex() {
        val index = TileRingIndex.build(emptyList())
        assertTrue(index.query(0, 0, 0).isEmpty())
        assertTrue(index.query(154, 79, 8).isEmpty())
    }

    @Test
    fun emptySingleton() {
        val a = TileRingIndex.EMPTY
        val b = TileRingIndex.EMPTY
        assertSame(a, b, "EMPTY should be a singleton")
    }

    // ── Single ring at zoom 8 ────────────────────────────────────────────

    @Test
    fun singleRingFoundAtCorrectTileZoom8() {
        val moscowRing = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscowRing))

        val x = lngToTileX(37.5, 8)
        val y = latToTileY(55.5, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(moscowRing), "Moscow ring should be found at tile ($x, $y, 8)")
    }

    @Test
    fun ringNotFoundAtDistantTile() {
        val moscowRing = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscowRing))

        val result = index.query(80, 140, 8)
        assertTrue(result.isEmpty(), "Moscow ring should not appear in South America tile")
    }

    // ── Multiple rings at zoom 8 ─────────────────────────────────────────

    @Test
    fun multipleRingsFilteredCorrectly() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val paris = ring(48.0, 49.0, 2.0, 3.0)
        val index = TileRingIndex.build(listOf(moscow, paris))

        val mxX = lngToTileX(37.5, 8)
        val mxY = latToTileY(55.5, 8)
        val moscowTile = index.query(mxX, mxY, 8)
        assertTrue(moscowTile.contains(moscow))
        assertTrue(!moscowTile.contains(paris), "Paris ring should not appear in Moscow tile")

        val pxX = lngToTileX(2.5, 8)
        val pxY = latToTileY(48.5, 8)
        val parisTile = index.query(pxX, pxY, 8)
        assertTrue(parisTile.contains(paris), "Paris ring should be at ($pxX, $pxY, 8)")
        assertTrue(!parisTile.contains(moscow), "Moscow ring should not appear in Paris tile")
    }

    @Test
    fun overlappingRingsBothReturnedForSameTile() {
        val ring1 = ring(55.0, 56.0, 37.0, 38.0)
        val ring2 = ring(55.2, 55.8, 37.2, 37.8)
        val index = TileRingIndex.build(listOf(ring1, ring2))

        val x = lngToTileX(37.5, 8)
        val y = latToTileY(55.5, 8)
        val result = index.query(x, y, 8)
        assertEquals(2, result.size, "Both overlapping rings should be returned")
        assertTrue(result.contains(ring1))
        assertTrue(result.contains(ring2))
    }

    @Test
    fun identicalBboxRingsBothIndexed() {
        val ring1 = ring(55.0, 56.0, 37.0, 38.0)
        val ring2 = MapRingDomain(
            points = listOf(GeoPoint(55.5, 37.5)),
            fillArgb = 0x4000FF00,
            strokeArgb = 0xFF00FF00.toInt(),
            minLat = 55.0,
            maxLat = 56.0,
            minLng = 37.0,
            maxLng = 38.0,
        )
        val index = TileRingIndex.build(listOf(ring1, ring2))

        val x = lngToTileX(37.5, 8)
        val y = latToTileY(55.5, 8)
        val result = index.query(x, y, 8)
        assertEquals(2, result.size, "Both rings with same bbox should be returned")
    }

    // ── Zoom 0 (world tile) ──────────────────────────────────────────────

    @Test
    fun zoom0SingleTileContainsAllRings() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val paris = ring(48.0, 49.0, 2.0, 3.0)
        val index = TileRingIndex.build(listOf(moscow, paris))

        val result = index.query(0, 0, 0)
        assertEquals(2, result.size, "Zoom 0 tile (0,0) should contain all rings")
    }

    // ── Intermediate zoom levels (1–7) ───────────────────────────────────

    @Test
    fun ringFoundAtAllPrecomputedZoomLevels() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        for (zoom in 0..8) {
            val x = lngToTileX(37.5, zoom)
            val y = latToTileY(55.5, zoom)
            val result = index.query(x, y, zoom)
            assertTrue(
                result.contains(moscow),
                "Moscow ring should be found at zoom $zoom tile ($x, $y)"
            )
        }
    }

    // ── Zoom > MAX_ZOOM (parent fallback with bbox filtering) ────────────

    @Test
    fun zoomAboveMaxUsesParentLookup() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        val x = lngToTileX(37.5, 10)
        val y = latToTileY(55.5, 10)
        val insideTile = index.query(x, y, 10)
        assertTrue(insideTile.contains(moscow), "Ring should be found via parent lookup at zoom 10 ($x, $y)")

        val outsideTile = index.query(0, 0, 10)
        assertTrue(outsideTile.isEmpty(), "Ring should not appear in distant zoom-10 tile")
    }

    @Test
    fun zoomAboveMaxFiltersOutNonOverlappingRings() {
        val leftOnly = ring(55.0, 55.5, 36.6, 37.0)
        val index = TileRingIndex.build(listOf(leftOnly))

        val leftX = lngToTileX(36.8, 9)
        val leftY = latToTileY(55.25, 9)
        val leftTile = index.query(leftX, leftY, 9)
        assertTrue(leftTile.contains(leftOnly), "Ring should appear in tile covering its bbox at zoom 9")

        val rightX = leftX + 1
        val rightTile = index.query(rightX, leftY, 9)
        assertTrue(rightTile.isEmpty(), "Ring should NOT appear in adjacent right tile at zoom 9")
    }

    @Test
    fun highZoomDistantTileReturnsEmpty() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        val result = index.query(16000, 16000, 15)
        assertTrue(result.isEmpty())
    }

    @Test
    fun zoom12ReturnsRingInsideBbox() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        val x = lngToTileX(37.5, 12)
        val y = latToTileY(55.5, 12)
        val result = index.query(x, y, 12)
        assertTrue(result.contains(moscow), "Ring should be found at zoom 12 ($x, $y)")
    }

    @Test
    fun zoom12ReturnsEmptyOutsideBbox() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        val x = lngToTileX(10.0, 12)
        val y = latToTileY(45.0, 12)
        val result = index.query(x, y, 12)
        assertTrue(result.isEmpty(), "Ring should not appear at zoom 12 far from Moscow")
    }

    @Test
    fun parentLookupWorksAtMultipleHighZoomLevels() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        for (zoom in 9..15) {
            val x = lngToTileX(37.5, zoom)
            val y = latToTileY(55.5, zoom)
            val result = index.query(x, y, zoom)
            assertTrue(
                result.contains(moscow),
                "Moscow ring should be found at zoom $zoom tile ($x, $y)"
            )
        }
    }

    @Test
    fun parentLookupDoesNotReturnRingsFromDifferentParent() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val paris = ring(48.0, 49.0, 2.0, 3.0)
        val index = TileRingIndex.build(listOf(moscow, paris))

        val px = lngToTileX(2.5, 10)
        val py = latToTileY(48.5, 10)
        val parisTile = index.query(px, py, 10)
        assertTrue(parisTile.contains(paris), "Paris ring should be found at zoom 10")
        assertTrue(!parisTile.contains(moscow), "Moscow ring should NOT appear in Paris zoom-10 tile")
    }

    @Test
    fun zoomAboveMaxSelectiveFilteringWithMultipleRings() {
        val left = ring(55.0, 55.5, 36.5, 37.0)
        val right = ring(55.0, 55.5, 38.0, 38.5)
        val index = TileRingIndex.build(listOf(left, right))

        val lx = lngToTileX(36.75, 10)
        val ly = latToTileY(55.25, 10)
        val leftResult = index.query(lx, ly, 10)
        assertTrue(leftResult.contains(left), "Left ring should be found at its zoom-10 tile")
        assertTrue(!leftResult.contains(right), "Right ring should not appear in left zoom-10 tile")

        val rx = lngToTileX(38.25, 10)
        val ry = latToTileY(55.25, 10)
        val rightResult = index.query(rx, ry, 10)
        assertTrue(rightResult.contains(right), "Right ring should be found at its zoom-10 tile")
        assertTrue(!rightResult.contains(left), "Left ring should not appear in right zoom-10 tile")
    }

    // ── Large ring spanning many tiles ────────────────────────────────────

    @Test
    fun largeRingSpansMultipleTilesAtZoom8() {
        val large = ring(45.0, 65.0, 25.0, 55.0)
        val index = TileRingIndex.build(listOf(large))

        var foundCount = 0
        for (x in 140..165) {
            for (y in 70..95) {
                if (index.query(x, y, 8).isNotEmpty()) foundCount++
            }
        }
        assertTrue(foundCount > 10, "Large ring should span many tiles, found $foundCount")
    }

    @Test
    fun largeRingCoveredAtZoom0() {
        val large = ring(45.0, 65.0, 25.0, 55.0)
        val index = TileRingIndex.build(listOf(large))

        val result = index.query(0, 0, 0)
        assertTrue(result.contains(large))
    }

    // ── Geographic edge cases ────────────────────────────────────────────

    @Test
    fun ringAtEquatorPrimeMeridian() {
        val equator = ring(-1.0, 1.0, -1.0, 1.0)
        val index = TileRingIndex.build(listOf(equator))

        val x = lngToTileX(0.0, 8)
        val y = latToTileY(0.0, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(equator), "Ring at equator/prime meridian should be found")
    }

    @Test
    fun ringInSouthernHemisphere() {
        val sydney = ring(-34.0, -33.0, 150.0, 152.0)
        val index = TileRingIndex.build(listOf(sydney))

        val x = lngToTileX(151.0, 8)
        val y = latToTileY(-33.5, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(sydney), "Ring in southern hemisphere should be found")

        val farTile = index.query(0, 0, 8)
        assertTrue(farTile.isEmpty())
    }

    @Test
    fun ringInWesternHemisphere() {
        val nyc = ring(40.0, 41.0, -75.0, -73.0)
        val index = TileRingIndex.build(listOf(nyc))

        val x = lngToTileX(-74.0, 8)
        val y = latToTileY(40.5, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(nyc), "Ring in western hemisphere should be found")
    }

    @Test
    fun ringWithNegativeLongitude() {
        val london = ring(51.0, 52.0, -1.0, 1.0)
        val index = TileRingIndex.build(listOf(london))

        val x = lngToTileX(0.0, 8)
        val y = latToTileY(51.5, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(london), "Ring crossing prime meridian should be found")
    }

    @Test
    fun wideRingSpansManyLongitudeTiles() {
        val wide = ring(50.0, 51.0, -10.0, 40.0)
        val index = TileRingIndex.build(listOf(wide))

        val xLeft = lngToTileX(-5.0, 8)
        val xRight = lngToTileX(35.0, 8)
        val y = latToTileY(50.5, 8)
        assertTrue(index.query(xLeft, y, 8).contains(wide), "Wide ring should be in left tile")
        assertTrue(index.query(xRight, y, 8).contains(wide), "Wide ring should be in right tile")

        val xFar = lngToTileX(100.0, 8)
        assertTrue(index.query(xFar, y, 8).isEmpty(), "Wide ring should not appear at lng 100")
    }

    // ── Very small ring (sub-tile) ───────────────────────────────────────

    @Test
    fun verySmallRingFoundInSingleTile() {
        val tiny = ring(55.75, 55.76, 37.61, 37.62)
        val index = TileRingIndex.build(listOf(tiny))

        val x = lngToTileX(37.615, 8)
        val y = latToTileY(55.755, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(tiny), "Tiny ring should be found in its tile")

        val x2 = x + 5
        val result2 = index.query(x2, y, 8)
        assertTrue(result2.isEmpty(), "Tiny ring should not appear 5 tiles away")
    }

    // ── Ring at tile boundary ────────────────────────────────────────────

    @Test
    fun ringStraddlingTileBoundaryAppearsInBothTiles() {
        val x1 = lngToTileX(37.0, 8)
        val x2 = lngToTileX(38.0, 8)
        assertTrue(x1 != x2, "Precondition: ring must span 2 tile columns at zoom 8")

        val boundary = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(boundary))

        val y = latToTileY(55.5, 8)
        val left = index.query(x1, y, 8)
        val right = index.query(x2, y, 8)
        assertTrue(left.contains(boundary), "Ring should appear in left tile")
        assertTrue(right.contains(boundary), "Ring should appear in right tile")
    }

    // ── Many rings (stress) ──────────────────────────────────────────────

    @Test
    fun manyRingsIndexedCorrectly() {
        val rings = (0 until 50).map { i ->
            val baseLat = 40.0 + i * 0.5
            ring(baseLat, baseLat + 0.3, 10.0, 11.0)
        }
        val index = TileRingIndex.build(rings)

        val firstRing = rings.first()
        val x = lngToTileX(10.5, 8)
        val y = latToTileY(40.15, 8)
        val result = index.query(x, y, 8)
        assertTrue(result.contains(firstRing), "First ring should be findable")

        val lastRing = rings.last()
        val yLast = latToTileY(64.65, 8)
        val resultLast = index.query(x, yLast, 8)
        assertTrue(resultLast.contains(lastRing), "Last ring should be findable")
    }

    // ── Consistency ──────────────────────────────────────────────────────

    @Test
    fun sameQueryReturnsConsistentResults() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val index = TileRingIndex.build(listOf(moscow))

        val x = lngToTileX(37.5, 8)
        val y = latToTileY(55.5, 8)
        val first = index.query(x, y, 8)
        val second = index.query(x, y, 8)
        assertEquals(first, second, "Same query should return same results")
    }

    // ── TileRingQuery interface contract ─────────────────────────────────

    @Test
    fun tileRingQueryLambdaWorks() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val query = TileRingQuery { _, _, _ -> listOf(moscow) }
        val result = query.query(0, 0, 0)
        assertEquals(1, result.size)
        assertSame(moscow, result[0])
    }

    @Test
    fun tileRingIndexCanBeWrappedAsTileRingQuery() {
        val moscow = ring(55.0, 56.0, 37.0, 38.0)
        val tileIndex = TileRingIndex.build(listOf(moscow))
        val query: TileRingQuery = TileRingQuery(tileIndex::query)

        val x = lngToTileX(37.5, 8)
        val y = latToTileY(55.5, 8)
        val result = query.query(x, y, 8)
        assertTrue(result.contains(moscow), "Wrapped TileRingIndex should work as TileRingQuery")
    }

    // ── Antimeridian (date line) edge case ───────────────────────────────

    @Test
    fun ringCrossingAntimeridianIsNotIndexed() {
        // minLng > maxLng when crossing the 180/-180 boundary.
        // The build loop's `for (tx in tx0..tx1)` produces an empty range,
        // so the ring is silently dropped. This test documents the limitation.
        val dateLine = ring(50.0, 52.0, 179.0, -179.0)
        val index = TileRingIndex.build(listOf(dateLine))

        val xEast = lngToTileX(179.5, 8)
        val y = latToTileY(51.0, 8)
        val xWest = lngToTileX(-179.5, 8)
        val eastResult = index.query(xEast, y, 8)
        val westResult = index.query(xWest, y, 8)
        // Current behavior: ring is not indexed at all because minLng > maxLng
        assertTrue(eastResult.isEmpty() && westResult.isEmpty(),
            "Antimeridian-crossing ring is not indexed (known limitation)")
    }
}
