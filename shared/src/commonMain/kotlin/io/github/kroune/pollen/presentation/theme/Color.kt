package io.github.kroune.pollen.presentation.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import io.github.kroune.pollen.theme.Accent
import io.github.kroune.pollen.theme.Accent2
import io.github.kroune.pollen.theme.AccentLight
import io.github.kroune.pollen.theme.CardWhite
import io.github.kroune.pollen.theme.Ink
import io.github.kroune.pollen.theme.Ink2
import io.github.kroune.pollen.theme.Line
import io.github.kroune.pollen.theme.Line2
import io.github.kroune.pollen.theme.Paper
import io.github.kroune.pollen.theme.Paper2
import io.github.kroune.pollen.theme.Severity1
import io.github.kroune.pollen.theme.Severity4

// Re-export PollenColors and LocalPollenColors from :theme module
public typealias PollenColors = io.github.kroune.pollen.theme.PollenColors
public val LocalPollenColors = io.github.kroune.pollen.theme.LocalPollenColors

val PollenLightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = CardWhite,
    primaryContainer = Accent2,
    onPrimaryContainer = CardWhite,
    secondary = Accent2,
    onSecondary = CardWhite,
    secondaryContainer = AccentLight,
    onSecondaryContainer = Accent,
    tertiary = Ink2,
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
