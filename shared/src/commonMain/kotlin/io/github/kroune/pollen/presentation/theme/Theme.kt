package io.github.kroune.pollen.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.pollen.presentation.common.ProvideShimmer

val PollenTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 44.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.8).sp,
        lineHeight = 48.sp,
    ),
    displayMedium = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.4).sp,
        lineHeight = 36.sp,
    ),
    displaySmall = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.3).sp,
        lineHeight = 34.sp,
    ),
    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 30.sp,
    ),
    headlineMedium = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp,
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.2.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
    ),
)

@Composable
fun PollenTheme(content: @Composable () -> Unit) {
    val pollenColors = PollenColors()
    val pollenShapes = PollenShapes()

    CompositionLocalProvider(
        LocalPollenColors provides pollenColors,
        LocalPollenShapes provides pollenShapes,
    ) {
        MaterialTheme(
            colorScheme = PollenLightColorScheme,
            shapes = PollenM3Shapes,
            typography = PollenTypography,
        ) {
            ProvideShimmer(content = content)
        }
    }
}

object PollenTheme {
    val colors: PollenColors
        @Composable get() = LocalPollenColors.current
    val shapes: PollenShapes
        @Composable get() = LocalPollenShapes.current
}

fun Modifier.pollenCardShadow(): Modifier = this.shadow(
    elevation = 4.dp,
    shape = RoundedCornerShape(14.dp),
    ambientColor = Color.Black.copy(alpha = 0.06f),
    spotColor = Color.Black.copy(alpha = 0.10f),
)

fun Modifier.pollenElevatedShadow(): Modifier = this.shadow(
    elevation = 8.dp,
    shape = RoundedCornerShape(14.dp),
    ambientColor = Color.Black.copy(alpha = 0.08f),
    spotColor = Color.Black.copy(alpha = 0.12f),
)
