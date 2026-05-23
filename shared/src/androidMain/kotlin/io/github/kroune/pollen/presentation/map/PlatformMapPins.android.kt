package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import io.github.kroune.pollen.domain.model.MapPinDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class PinClusterItem(
    val pin: MapPinDomain,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(pin.latitude, pin.longitude)
    override fun getTitle(): String = pin.tags.joinToString(" ")
    override fun getSnippet(): String = pin.date.toString()
    override fun getZIndex(): Float = 0f
}

@Stable
actual class PlatformMapPins(val items: ImmutableList<PinClusterItem>)

actual fun List<MapPinDomain>.toPlatformMapPins(): PlatformMapPins =
    PlatformMapPins(map { PinClusterItem(it) }.toImmutableList())
