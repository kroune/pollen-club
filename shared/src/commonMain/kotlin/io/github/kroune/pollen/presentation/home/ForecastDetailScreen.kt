package io.github.kroune.pollen.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.compose.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.compose.cartesian.Scroll
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartRanges
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerDimensions
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.DashedShape
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.presentation.diary.monthShortStringDesc
import io.github.kroune.pollen.domain.model.LoadState
import org.jetbrains.compose.resources.DrawableResource
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.ForecastDetailChartSkeleton
import io.github.kroune.pollen.presentation.common.ForecastDetailHeaderSkeleton
import io.github.kroune.pollen.presentation.common.ForecastDetailStatsSkeleton
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.shimmerEffect
import io.github.kroune.pollen.presentation.detail.DetailStatsUi
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate

@Composable
fun ForecastDetailScreen(
    viewModel: ForecastDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    val pollenState = state.pollen

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        when (pollenState) {
            is LoadState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).statusBarsPadding(),
                ) {
                    ForecastDetailHeaderSkeleton()
                    Spacer(Modifier.height(20.dp))
                    ForecastDetailChartSkeleton()
                    Spacer(Modifier.height(16.dp))
                    ForecastDetailStatsSkeleton(Modifier.padding(horizontal = 16.dp))
                }
            }
            is LoadState.Failed -> {
                Box(Modifier.padding(innerPadding)) {
                    FullScreenError(onRetry = viewModel::loadData)
                }
            }
            is LoadState.Loaded -> {
                DetailContent(
                    state = state,
                    pollenName = pollenState.data.name,
                    pollenIcon = state.pollenIconRes,
                    onBack = onBack,
                    onToggleFeeling = viewModel::toggleFeelingLine,
                    onRetry = viewModel::loadData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    state: ForecastDetailUiState,
    pollenName: String,
    pollenIcon: DrawableResource?,
    onBack: () -> Unit,
    onToggleFeeling: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val severity4Color = PollenTheme.colors.severity4
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        // Back + title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(MR.strings.back),
                    tint = PollenTheme.colors.ink2,
                )
            }
            Text(
                text = pollenName,
                style = MaterialTheme.typography.displaySmall,
            )
        }

        // Score row
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = state.currentScore,
                fontSize = 40.sp,
                fontWeight = FontWeight.Normal,
                color = PollenTheme.colors.severityColor(state.severityLevel),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(MR.strings.forecast_score_today, state.currentScoreMax),
                fontSize = 14.sp,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(bottom = 5.dp),
            )
            Spacer(Modifier.weight(1f))
            SeverityLabel(
                level = state.severityLevel,
                label = state.severityLabel,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Chart header + legend
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(MR.strings.forecast_daily_dynamics).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = PollenTheme.colors.ink3,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = PollenTheme.colors.ink3,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(MR.strings.forecast_swipe_hint),
                        fontSize = 10.sp,
                        color = PollenTheme.colors.ink3,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Pollen legend
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier
                            .width(18.dp)
                            .height(3.dp)
                            .background(PollenTheme.colors.ink, RoundedCornerShape(2.dp))
                    )
                    Text(stringResource(MR.strings.forecast_pollen_legend), fontSize = 13.sp, color = PollenTheme.colors.ink2)
                }

                // Feeling legend (toggleable)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable(onClick = onToggleFeeling),
                ) {
                    Canvas(modifier = Modifier.width(18.dp).height(2.dp)) {
                        drawLine(
                            color = severity4Color,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())),
                        )
                    }
                    Text(
                        stringResource(MR.strings.forecast_feeling_legend),
                        fontSize = 13.sp,
                        color = PollenTheme.colors.ink2.copy(alpha = if (state.showFeelingLine) 1f else 0.4f),
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (state.showFeelingLine) PollenTheme.colors.accent
                                else Color.Transparent,
                            )
                            .then(
                                if (!state.showFeelingLine) Modifier.border(1.5.dp, PollenTheme.colors.line, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.showFeelingLine) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Chart
        val pollenLoaded = state.pollen as? LoadState.Loaded
        when (val timeline = state.timeline) {
            is LoadState.Loading -> {
                Box(
                    Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect(),
                )
            }
            is LoadState.Failed -> {
                Box(
                    Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 16.dp),
                ) {
                    FullScreenError(onRetry = onRetry)
                }
            }
            is LoadState.Loaded -> {
                if (timeline.data.isEmpty()) {
                    Box(
                        Modifier.fillMaxWidth().height(220.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(MR.strings.forecast_no_data), color = PollenTheme.colors.ink3)
                    }
                } else if (pollenLoaded != null) {
                    DetailChart(
                        timeline = timeline.data,
                        maxLevel = pollenLoaded.data.maxLevel,
                        today = state.today,
                        showFeelingLine = state.showFeelingLine,
                        feelingValues = state.feelingValues,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats card
        val stats = state.stats
        if (stats != null) {
            StatsCard(
                stats = stats,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // About section
        if (state.aboutText.isNotBlank()) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(MR.strings.forecast_about_period).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.aboutText,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp,
                color = PollenTheme.colors.ink2,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SeverityLabel(
    level: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(PollenTheme.colors.severityColor(level), CircleShape),
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.severityColor(level),
            letterSpacing = 0.2.sp,
        )
    }
}

@Composable
private fun StatsCard(
    stats: DetailStatsUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatItem(
                label = stringResource(MR.strings.forecast_stat_peak),
                value = formatShortDate(stats.peakDate),
                modifier = Modifier.weight(1f),
            )
            VerticalDivider()
            StatItem(
                label = stringResource(MR.strings.forecast_stat_decline),
                value = stats.declineDate?.let { "~${formatShortDate(it)}" } ?: "—",
                modifier = Modifier.weight(1f),
            )
            VerticalDivider()
            StatItem(
                label = stringResource(MR.strings.forecast_stat_yours),
                value = stringResource(MR.strings.forecast_stat_symptoms, stats.symptomCount),
                valueColor = PollenTheme.colors.severity3,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = PollenTheme.colors.ink,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.ink3,
            letterSpacing = 0.4.sp,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .width(1.dp)
            .height(32.dp)
            .background(PollenTheme.colors.line2),
    )
}

@Composable
private fun formatShortDate(dateStr: String): String {
    val parsed = remember(dateStr) {
        runCatching { LocalDate.parse(dateStr) }.getOrNull()
    }
    if (parsed == null) return dateStr
    val month = monthShortStringDesc(parsed.month).localized()
    return "${parsed.day} $month"
}

@Composable
private fun DetailChart(
    timeline: ImmutableList<LevelDomain>,
    maxLevel: Int,
    today: LocalDate,
    showFeelingLine: Boolean,
    feelingValues: ImmutableList<Int?>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val dateLabels = remember(timeline) {
        timeline.mapIndexed { index, level ->
            val day = level.date.takeLast(2).trimStart('0')
            index to day
        }.toMap()
    }

    val todayIndex = remember(timeline, today) {
        val todayStr = today.toString()
        timeline.indexOfFirst { it.date == todayStr }
    }

    val hasAnyFeelingData = feelingValues.any { it != null }

    LaunchedEffect(timeline, feelingValues) {
        modelProducer.runTransaction {
            lineSeries {
                series(timeline.map { it.value })
                if (hasAnyFeelingData) {
                    val feelX = mutableListOf<Number>()
                    val feelY = mutableListOf<Number>()
                    feelingValues.forEachIndexed { i, v ->
                        if (v != null) {
                            feelX.add(i)
                            feelY.add(v)
                        }
                    }
                    if (feelX.isNotEmpty()) {
                        series(x = feelX, y = feelY)
                    }
                }
            }
        }
    }

    val inkColor = PollenTheme.colors.ink
    val ink3Color = PollenTheme.colors.ink3
    val line2Color = PollenTheme.colors.line2
    val sev0 = PollenTheme.colors.severity0
    val sev1 = PollenTheme.colors.severity1
    val sev2 = PollenTheme.colors.severity2
    val sev3 = PollenTheme.colors.severity3
    val sev4 = PollenTheme.colors.severity4
    val sev5 = PollenTheme.colors.severity5
    val severityList = listOf(sev0, sev1, sev2, sev3, sev4, sev5)

    val gradientBrush = remember(severityList) {
        Brush.verticalGradient(
            colors = severityList.reversed().map { it.copy(alpha = 0.35f) }
        )
    }

    val normalPoints = remember(severityList) {
        severityList.mapIndexed { level, color ->
            level to LineCartesianLayer.Point(
                component = ShapeComponent(
                    fill = Fill(color),
                    shape = RoundedCornerShape(50),
                ),
                size = 5.dp,
            )
        }.toMap()
    }

    val todayPoints = remember(severityList) {
        severityList.mapIndexed { level, color ->
            level to LineCartesianLayer.Point(
                component = ShapeComponent(
                    fill = Fill(color),
                    strokeFill = Fill(Color.White),
                    strokeThickness = 1.6.dp,
                    shape = RoundedCornerShape(50),
                ),
                size = 9.dp,
            )
        }.toMap()
    }

    val pollenPointProvider = remember(todayIndex, normalPoints, todayPoints) {
        object : LineCartesianLayer.PointProvider {
            override fun getPoint(
                entry: LineCartesianLayerModel.Entry,
                seriesIndex: Int,
                extraStore: ExtraStore,
            ): LineCartesianLayer.Point {
                val level = entry.y.toInt().coerceIn(0, 5)
                return if (entry.x.toInt() == todayIndex) {
                    todayPoints[level] ?: todayPoints[0]!!
                } else {
                    normalPoints[level] ?: normalPoints[0]!!
                }
            }
            override fun getLargestPoint(extraStore: ExtraStore) = normalPoints[0]!!
        }
    }

    val pollenLine = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(inkColor)),
        stroke = LineCartesianLayer.LineStroke.Continuous(thickness = 1.8.dp),
        areaFill = LineCartesianLayer.AreaFill.single(Fill(gradientBrush)),
        pointProvider = pollenPointProvider,
    )

    val feelingLineColor = if (showFeelingLine) sev4 else Color.Transparent
    val feelingPointFill = if (showFeelingLine) Color.White else Color.Transparent
    val feelingPointStroke = if (showFeelingLine) sev4 else Color.Transparent

    val feelingPoint = remember(feelingPointFill, feelingPointStroke) {
        LineCartesianLayer.Point(
            component = ShapeComponent(
                fill = Fill(feelingPointFill),
                strokeFill = Fill(feelingPointStroke),
                strokeThickness = 1.4.dp,
                shape = RoundedCornerShape(50),
            ),
            size = 5.dp,
        )
    }

    val feelingPointProvider = remember(feelingPoint) {
        object : LineCartesianLayer.PointProvider {
            override fun getPoint(
                entry: LineCartesianLayerModel.Entry,
                seriesIndex: Int,
                extraStore: ExtraStore,
            ) = feelingPoint
            override fun getLargestPoint(extraStore: ExtraStore) = feelingPoint
        }
    }

    val feelingLine = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(feelingLineColor)),
        stroke = LineCartesianLayer.LineStroke.Dashed(
            thickness = 1.4.dp,
            dashLength = 4.dp,
            gapLength = 3.dp,
        ),
        areaFill = null,
        pointProvider = feelingPointProvider,
    )

    val lineProvider = if (hasAnyFeelingData) {
        LineCartesianLayer.LineProvider.series(pollenLine, feelingLine)
    } else {
        LineCartesianLayer.LineProvider.series(pollenLine)
    }

    val bottomFormatter = remember(dateLabels) {
        CartesianValueFormatter { _, value, _ ->
            dateLabels[value.toInt()] ?: ""
        }
    }

    val startFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            value.toInt().toString()
        }
    }

    val startAxisLabel = rememberTextComponent(
        style = TextStyle(color = ink3Color, fontSize = 10.sp),
        margins = Insets(start = 6.dp, end = 4.dp),
    )
    val bottomAxisLabel = rememberTextComponent(
        style = TextStyle(color = ink3Color, fontSize = 10.sp),
        margins = Insets(top = 6.dp),
    )
    val subtleGuideline = rememberLineComponent(
        fill = Fill(line2Color),
        thickness = 0.5.dp,
    )

    val todayMarkerLabel = rememberTextComponent(
        style = TextStyle(color = Color.Transparent, fontSize = 0.sp),
    )
    val todayMarkerGuideline = rememberLineComponent(
        fill = Fill(inkColor),
        thickness = 1.dp,
        shape = DashedShape(dashLength = 3.dp, gapLength = 2.dp),
    )
    val todayMarker = rememberDefaultCartesianMarker(
        label = todayMarkerLabel,
        guideline = todayMarkerGuideline,
    )

    val initialScroll = remember(todayIndex) {
        if (todayIndex >= 0) Scroll.Absolute.x(todayIndex.toDouble()) else Scroll.Absolute.End
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = lineProvider,
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minY = 0.0,
                    maxY = maxLevel.toDouble(),
                ),
            ),
            startAxis = VerticalAxis.rememberStart(
                line = null,
                label = startAxisLabel,
                tick = null,
                guideline = subtleGuideline,
                valueFormatter = startFormatter,
                itemPlacer = remember { VerticalAxis.ItemPlacer.step({ 1.0 }) },
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                line = null,
                label = bottomAxisLabel,
                tick = null,
                guideline = null,
                valueFormatter = bottomFormatter,
                itemPlacer = remember {
                    EdgeAwareItemPlacer(HorizontalAxis.ItemPlacer.aligned(addExtremeLabelPadding = false))
                },
            ),
            persistentMarkers = if (todayIndex >= 0) {
                { todayMarker at todayIndex }
            } else {
                null
            },
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(
            scrollEnabled = true,
            initialScroll = initialScroll,
        ),
        zoomState = rememberVicoZoomState(
            zoomEnabled = true,
            initialZoom = Zoom.x(14.0),
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
    )
}

private class EdgeAwareItemPlacer(
    private val delegate: HorizontalAxis.ItemPlacer,
) : HorizontalAxis.ItemPlacer {

    override fun getShiftExtremeLines(context: CartesianDrawingContext) =
        delegate.getShiftExtremeLines(context)

    override fun getFirstLabelValue(context: CartesianMeasuringContext, maxLabelWidth: Float) =
        delegate.getFirstLabelValue(context, maxLabelWidth)

    override fun getLastLabelValue(context: CartesianMeasuringContext, maxLabelWidth: Float) =
        delegate.getLastLabelValue(context, maxLabelWidth)

    override fun getLabelValues(
        context: CartesianDrawingContext,
        visibleXRange: ClosedFloatingPointRange<Double>,
        fullXRange: ClosedFloatingPointRange<Double>,
        maxLabelWidth: Float,
    ): List<Double> {
        val all = delegate.getLabelValues(context, visibleXRange, fullXRange, maxLabelWidth)
        val xSpacing = context.layerDimensions.xSpacing
        if (maxLabelWidth <= 0f || xSpacing <= 0f) return all
        val halfLabelX = maxLabelWidth / 2 / xSpacing * context.ranges.xStep
        return all.filter { v ->
            v - halfLabelX >= visibleXRange.start && v + halfLabelX <= visibleXRange.endInclusive
        }
    }

    override fun getWidthMeasurementLabelValues(
        context: CartesianMeasuringContext,
        layerDimensions: CartesianLayerDimensions,
        fullXRange: ClosedFloatingPointRange<Double>,
    ): List<Double> {
        val result = delegate.getWidthMeasurementLabelValues(context, layerDimensions, fullXRange)
        if (result.isNotEmpty()) return result
        return with(context.ranges) {
            buildList {
                add(minX)
                if (xLength >= xStep) add(minX + xStep * kotlin.math.floor(xLength / xStep))
                if (xLength >= 2 * xStep) add(minX + xStep * kotlin.math.floor(xLength / 2 / xStep))
            }
        }
    }

    override fun getHeightMeasurementLabelValues(
        context: CartesianMeasuringContext,
        layerDimensions: CartesianLayerDimensions,
        fullXRange: ClosedFloatingPointRange<Double>,
        maxLabelWidth: Float,
    ) = delegate.getHeightMeasurementLabelValues(context, layerDimensions, fullXRange, maxLabelWidth)

    override fun getLineValues(
        context: CartesianDrawingContext,
        visibleXRange: ClosedFloatingPointRange<Double>,
        fullXRange: ClosedFloatingPointRange<Double>,
        maxLabelWidth: Float,
    ) = delegate.getLineValues(context, visibleXRange, fullXRange, maxLabelWidth)

    override fun getStartLayerMargin(
        context: CartesianMeasuringContext,
        layerDimensions: CartesianLayerDimensions,
        tickThickness: Float,
        maxLabelWidth: Float,
    ) = delegate.getStartLayerMargin(context, layerDimensions, tickThickness, maxLabelWidth)

    override fun getEndLayerMargin(
        context: CartesianMeasuringContext,
        layerDimensions: CartesianLayerDimensions,
        tickThickness: Float,
        maxLabelWidth: Float,
    ) = delegate.getEndLayerMargin(context, layerDimensions, tickThickness, maxLabelWidth)
}
