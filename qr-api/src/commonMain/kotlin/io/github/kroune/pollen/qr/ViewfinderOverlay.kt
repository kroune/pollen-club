package io.github.kroune.pollen.qr

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.viewfinderOverlay(
    scanLineProgress: Float,
    accentColor: Color,
): Modifier = drawWithContent {
    drawContent()
    val bracketLen = 28.dp.toPx()
    val strokeWidth = 3.dp.toPx()
    val white = Color.White

    val corners = listOf(
        Offset(0f, 0f),
        Offset(size.width - bracketLen, 0f),
        Offset(0f, size.height - bracketLen),
        Offset(size.width - bracketLen, size.height - bracketLen),
    )

    corners.forEachIndexed { index, _ ->
        val isLeft = index % 2 == 0
        val isTop = index < 2
        val x = if (isLeft) 0f else size.width
        val y = if (isTop) 0f else size.height
        val hDir = if (isLeft) 1f else -1f
        val vDir = if (isTop) 1f else -1f

        drawLine(white, Offset(x, y), Offset(x + bracketLen * hDir, y), strokeWidth)
        drawLine(white, Offset(x, y), Offset(x, y + bracketLen * vDir), strokeWidth)
    }

    val lineY = 10.dp.toPx() + (size.height - 20.dp.toPx()) * scanLineProgress
    drawLine(
        color = accentColor.copy(alpha = 0.8f),
        start = Offset(10.dp.toPx(), lineY),
        end = Offset(size.width - 10.dp.toPx(), lineY),
        strokeWidth = 2.dp.toPx(),
    )
}
