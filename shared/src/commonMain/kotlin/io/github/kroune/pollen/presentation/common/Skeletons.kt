package io.github.kroune.pollen.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.pollen.presentation.theme.PollenTheme

@Composable
fun LocationHeaderSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Box(
            Modifier
                .fillMaxWidth(0.5f)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth(0.3f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
    }
}

@Composable
fun WeatherCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .shimmerEffect(),
    )
}

@Composable
fun PersonalIndexCardSkeleton(modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val scoreStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    val labelStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
    val scoreSize = remember(scoreStyle) {
        val result = textMeasurer.measure("0,0", scoreStyle)
        with(density) { result.size.width.toDp() to result.size.height.toDp() }
    }
    val labelSize = remember(labelStyle) {
        val result = textMeasurer.measure("Средняя", labelStyle)
        with(density) { result.size.width.toDp() to result.size.height.toDp() }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .padding(end = 10.dp)
                    .width(scoreSize.first)
                    .height(scoreSize.second)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                repeat(5) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect(),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .width(labelSize.first)
                    .height(labelSize.second)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
fun PollenLevelCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .shimmerEffect(),
    )
}

@Composable
fun PollenListSkeleton(count: Int = 5, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) {
            PollenLevelCardSkeleton()
        }
    }
}

@Composable
fun FeedCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .shimmerEffect(),
    )
}

@Composable
fun FeedListSkeleton(count: Int = 4, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(count) {
            FeedCardSkeleton()
        }
    }
}

@Composable
fun MedicationCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .shimmerEffect(),
    )
}

@Composable
fun MedicationListSkeleton(count: Int = 4, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Title placeholder
        Box(
            Modifier
                .fillMaxWidth(0.4f)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
        Spacer(Modifier.height(4.dp))
        repeat(count) {
            MedicationCardSkeleton()
        }
    }
}

@Composable
fun MapChipRowSkeleton(count: Int = 5, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(count) {
            Box(
                Modifier
                    .width(72.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
fun MapAreaSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(0.dp))
            .shimmerEffect(),
    )
}

@Composable
fun DayStripSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(7) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .width(24.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .width(16.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
                Spacer(Modifier.height(5.dp))
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .shimmerEffect(),
                )
            }
        }
    }
}

@Composable
fun ForecastDetailHeaderSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect(),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .width(140.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect(),
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Box(
                Modifier
                    .width(64.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .width(40.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .width(72.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
fun ForecastDetailChartSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
            Box(
                Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
        }
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect(),
        )
    }
}

@Composable
fun ForecastDetailStatsSkeleton(modifier: Modifier = Modifier) {
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
            repeat(3) { i ->
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .width(56.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(48.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                }
                if (i < 2) {
                    Box(
                        Modifier
                            .padding(horizontal = 12.dp)
                            .width(1.dp)
                            .height(32.dp)
                            .background(PollenTheme.colors.line2),
                    )
                }
            }
        }
    }
}
