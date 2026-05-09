package io.github.kroune.pollen.presentation.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF1B1F1D)
val Ink2 = Color(0xFF475550)
val Ink3 = Color(0xFF8A948F)
val Line = Color(0xFFD5DBD7)
val Line2 = Color(0xFFE8ECE9)
val Paper = Color(0xFFF7F6F2)
val Paper2 = Color(0xFFEFEEE9)
val CardWhite = Color(0xFFFFFFFF)

val Accent = Color(0xFF26873C)
val Accent2 = Color(0xFF2A5E40)
val AccentLight = Color(0x1426873C)

val Severity0 = Color(0xFFBCC8C0)
val Severity1 = Color(0xFF6EA85C)
val Severity2 = Color(0xFFE5A50A)
val Severity3 = Color(0xFFD4713A)
val Severity4 = Color(0xFFC43D3D)
val Severity5 = Color(0xFF7A3590)

@Immutable
data class PollenColors(
    val ink: Color = Ink,
    val ink2: Color = Ink2,
    val ink3: Color = Ink3,
    val line: Color = Line,
    val line2: Color = Line2,
    val paper: Color = Paper,
    val paper2: Color = Paper2,
    val card: Color = CardWhite,
    val accent: Color = Accent,
    val accent2: Color = Accent2,
    val accentLight: Color = AccentLight,
    val severity0: Color = Severity0,
    val severity1: Color = Severity1,
    val severity2: Color = Severity2,
    val severity3: Color = Severity3,
    val severity4: Color = Severity4,
    val severity5: Color = Severity5,
) {
    fun severityColor(level: Int): Color = when (level) {
        0 -> severity0
        1 -> severity1
        2 -> severity2
        3 -> severity3
        4 -> severity4
        5 -> severity5
        else -> severity0
    }
}

val LocalPollenColors = staticCompositionLocalOf { PollenColors() }

val PollenLightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = CardWhite,
    primaryContainer = Accent2,
    onPrimaryContainer = CardWhite,
    secondary = Accent2,
    onSecondary = CardWhite,
    secondaryContainer = AccentLight,
    onSecondaryContainer = Accent,
    tertiary = Ink3,
    onTertiary = CardWhite,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Paper2,
    onSurfaceVariant = Ink2,
    outline = Line,
    outlineVariant = Line2,
    error = Severity4,
    onError = CardWhite,
    errorContainer = Color(0xFFFCE8E8),
    onErrorContainer = Ink,
    surfaceContainerLowest = CardWhite,
    surfaceContainerLow = CardWhite,
    surfaceContainer = Paper,
    surfaceContainerHigh = Paper2,
    surfaceContainerHighest = Paper2,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
    inversePrimary = Severity1,
    scrim = Color(0xFF000000),
)
