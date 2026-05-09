package io.github.kroune.pollen.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class PollenShapes(
    val card: Shape = RoundedCornerShape(14.dp),
    val small: Shape = RoundedCornerShape(10.dp),
    val extraSmall: Shape = RoundedCornerShape(7.dp),
    val pill: Shape = RoundedCornerShape(100.dp),
)

val LocalPollenShapes = staticCompositionLocalOf { PollenShapes() }

val PollenM3Shapes = Shapes(
    extraSmall = RoundedCornerShape(7.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(100.dp),
)
