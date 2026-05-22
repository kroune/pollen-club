package io.github.kroune.pollen.presentation.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.pollen.presentation.theme.PollenTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalBox
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PollenLevelDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.number

@Composable
fun ForecastChart(
    timeline: ImmutableList<LevelDomain>,
    pollenLevels: List<PollenLevelDomain>,
    maxLevel: Int,
    modifier: Modifier = Modifier,
) {
    if (timeline.isEmpty()) return

    val modelProducer = remember { CartesianChartModelProducer() }

    val dateLabels = remember(timeline) {
        timeline.mapIndexed { index, level ->
            val mm = level.date.month.number.toString().padStart(2, '0')
            val dd = level.date.day.toString().padStart(2, '0')
            index to "$mm-$dd"
        }.toMap()
    }

    LaunchedEffect(timeline) {
        modelProducer.runTransaction {
            lineSeries {
                series(timeline.map { it.value })
            }
        }
    }

    val sortedLevels = remember(pollenLevels) {
        pollenLevels.sortedBy { it.level }
    }

    val decorations = remember(sortedLevels, maxLevel) {
        sortedLevels.mapIndexed { index, level ->
            val yBottom = level.level.toDouble()
            val yTop = if (index < sortedLevels.lastIndex) {
                sortedLevels[index + 1].level.toDouble()
            } else {
                maxLevel.toDouble()
            }
            val zoneColor = if (level.color != 0) {
                Color(level.color).copy(alpha = 0.22f)
            } else {
                Color.Gray.copy(alpha = 0.08f)
            }
            HorizontalBox(
                y = { yBottom..yTop },
                box = ShapeComponent(
                    fill = Fill(zoneColor),
                    shape = RoundedCornerShape(0.dp),
                ),
            )
        }
    }

    val levelNameMap = remember(sortedLevels) {
        sortedLevels.associate { it.level to it.name }
    }

    val bottomFormatter = remember(dateLabels) {
        CartesianValueFormatter { _, value, _ ->
            dateLabels[value.toInt()] ?: ""
        }
    }

    val startFormatter = remember(levelNameMap) {
        CartesianValueFormatter { _, value, _ ->
            levelNameMap[value.toInt()] ?: value.toInt().toString()
        }
    }

    val lineColor = PollenTheme.colors.accent

    val axisLabel = rememberTextComponent(
        style = TextStyle(color = PollenTheme.colors.ink3, fontSize = 11.sp),
    )
    val subtleGuideline = rememberLineComponent(
        fill = Fill(PollenTheme.colors.line2),
        thickness = 0.5.dp,
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                        stroke = LineCartesianLayer.LineStroke.Continuous(thickness = 2.5.dp),
                        areaFill = LineCartesianLayer.AreaFill.single(Fill(lineColor.copy(alpha = 0.08f))),
                        pointProvider = LineCartesianLayer.PointProvider.single(
                            LineCartesianLayer.Point(
                                component = ShapeComponent(
                                    fill = Fill(lineColor),
                                    shape = RoundedCornerShape(50),
                                ),
                                size = 5.dp,
                            ),
                        ),
                    ),
                ),
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minY = 0.0,
                    maxY = maxLevel.toDouble(),
                ),
            ),
            startAxis = VerticalAxis.rememberStart(
                line = null,
                label = axisLabel,
                tick = null,
                guideline = subtleGuideline,
                valueFormatter = startFormatter,
                itemPlacer = remember(sortedLevels) {
                    VerticalAxis.ItemPlacer.count({ sortedLevels.size.coerceAtLeast(2) })
                },
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                line = null,
                label = axisLabel,
                tick = null,
                guideline = null,
                valueFormatter = bottomFormatter,
            ),
            decorations = decorations,
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
    )
}
