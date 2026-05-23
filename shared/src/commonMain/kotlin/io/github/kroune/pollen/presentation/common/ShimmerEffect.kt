package io.github.kroune.pollen.presentation.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import co.touchlab.kermit.Logger
import io.github.kroune.pollen.presentation.theme.PollenTheme

private const val SHIMMER_DURATION_MS = 1200
private const val SHIMMER_TRANSLATE_DISTANCE = 1000f
private const val SHIMMER_GRADIENT_WIDTH = 200f

@Stable
class ShimmerState internal constructor(
    internal val progress: State<Float>,
)

val LocalShimmerState = staticCompositionLocalOf<ShimmerState?> { null }

@Composable
fun ProvideShimmer(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition()
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )
    val state = remember { ShimmerState(progress) }
    CompositionLocalProvider(LocalShimmerState provides state) {
        content()
    }
}

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val baseColor = PollenTheme.colors.line2
    val highlightColor = PollenTheme.colors.paper2
    val progressState = LocalShimmerState.current?.progress
    if (progressState == null) {
        Logger.withTag("ShimmerEffect").w {
            "shimmerEffect() used outside ProvideShimmer — shimmer will not animate"
        }
        return this
    }
    return this then ShimmerElement(
        baseColor = baseColor,
        highlightColor = highlightColor,
        progressState = progressState,
    )
}

private class ShimmerElement(
    val baseColor: Color,
    val highlightColor: Color,
    val progressState: State<Float>,
) : ModifierNodeElement<ShimmerNode>() {
    override fun create() = ShimmerNode(baseColor, highlightColor, progressState)

    override fun update(node: ShimmerNode) {
        node.update(baseColor, highlightColor, progressState)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "shimmerEffect"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShimmerElement) return false
        return baseColor == other.baseColor && highlightColor == other.highlightColor
    }

    override fun hashCode(): Int {
        var result = baseColor.hashCode()
        result = 31 * result + highlightColor.hashCode()
        return result
    }
}

private class ShimmerNode(
    private var baseColor: Color,
    private var highlightColor: Color,
    private var progressState: State<Float>,
) : Modifier.Node(), DrawModifierNode {

    private var colors: List<Color> = buildColors()

    private fun buildColors() = listOf(baseColor, highlightColor, baseColor)

    fun update(baseColor: Color, highlightColor: Color, progressState: State<Float>) {
        val colorsChanged = this.baseColor != baseColor || this.highlightColor != highlightColor
        val stateChanged = this.progressState !== progressState
        if (colorsChanged) {
            this.baseColor = baseColor
            this.highlightColor = highlightColor
            this.colors = buildColors()
        }
        if (stateChanged) {
            this.progressState = progressState
        }
        if (colorsChanged || stateChanged) {
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        val translateAnim = progressState.value * SHIMMER_TRANSLATE_DISTANCE
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(
                    translateAnim - SHIMMER_GRADIENT_WIDTH,
                    translateAnim - SHIMMER_GRADIENT_WIDTH,
                ),
                end = Offset(translateAnim, translateAnim),
            ),
        )
        drawContent()
    }
}
