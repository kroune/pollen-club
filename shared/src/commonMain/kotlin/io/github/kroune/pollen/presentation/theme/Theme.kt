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

val PollenTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.8).sp,
        lineHeight = 36.sp,
    ),
    displayMedium = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.4).sp,
        lineHeight = 29.sp,
    ),
    displaySmall = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.3).sp,
        lineHeight = 26.sp,
    ),
    headlineLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
    ),
    headlineMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp,
    ),
    headlineSmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
    ),
    titleLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
    ),
    titleMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp,
    ),
    titleSmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 17.sp,
    ),
    bodyLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 21.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 19.5.sp,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
    ),
    labelMedium = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.2.sp,
        lineHeight = 14.sp,
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 14.sp,
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
            content = content,
        )
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
