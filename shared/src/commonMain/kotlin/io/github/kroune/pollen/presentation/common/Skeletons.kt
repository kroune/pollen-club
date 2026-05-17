package io.github.kroune.pollen.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
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
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
    val textSize = remember(textStyle) {
        val result = textMeasurer.measure("Москва, ЦАО", textStyle)
        with(density) { result.size.width.toDp() to result.size.height.toDp() }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .width(textSize.first)
                    .height(textSize.second)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.width(2.dp))
            Box(Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
    }
}

@Composable
fun WeatherCardSkeleton(modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val displaySmallStyle = MaterialTheme.typography.displaySmall
    val titleMediumStyle = MaterialTheme.typography.titleMedium

    val tempSize = remember(displaySmallStyle) {
        val result = textMeasurer.measure("24°", displaySmallStyle)
        with(density) { result.size.width.toDp() to result.size.height.toDp() }
    }
    val weatherSize = remember(titleMediumStyle) {
        val result = textMeasurer.measure("Облачно", titleMediumStyle)
        with(density) { result.size.width.toDp() to result.size.height.toDp() }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .width(tempSize.first)
                    .height(tempSize.second)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.width(20.dp))
            Box(
                Modifier
                    .width(weatherSize.first)
                    .height(weatherSize.second)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
fun PersonalIndexCardSkeleton(modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val scoreStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(5) {
                    Box(Modifier.size(8.dp).clip(CircleShape).shimmerEffect())
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
fun PollenListSkeleton(count: Int = 3, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            repeat(count) { index ->
                if (index > 0) {
                    HorizontalDivider(color = PollenTheme.colors.line2)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(5) {
                            Box(Modifier.size(8.dp).clip(CircleShape).shimmerEffect())
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect(),
                    )
                }
            }
        }
    }
}

@Composable
fun FeedCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(CircleShape).shimmerEffect())
                Spacer(Modifier.width(12.dp))
                Column {
                    Box(
                        Modifier
                            .width(120.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(72.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect(),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
        }
    }
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
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PollenTheme.colors.card, shape)
            .border(1.dp, PollenTheme.colors.line2, shape),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(34.dp).clip(CircleShape).shimmerEffect())
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    Modifier
                        .width(90.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .width(70.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .width(100.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .width(52.dp)
                    .height(26.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
fun MedicationListSkeleton(count: Int = 4, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) {
            MedicationCardSkeleton()
        }
    }
}

@Composable
fun CategoriesCardSkeleton(count: Int = 3, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        repeat(count) { index ->
            if (index > 0) {
                HorizontalDivider(color = PollenTheme.colors.line2)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect(),
                )
            }
        }
    }
}

@Composable
fun FriendsListSkeleton(count: Int = 3, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .width(100.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
            Box(
                Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect(),
            )
        }
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column {
                repeat(count) { index ->
                    if (index > 0) {
                        HorizontalDivider(color = PollenTheme.colors.line2)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(34.dp).clip(CircleShape).shimmerEffect())
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                Modifier
                                    .width(100.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerEffect(),
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .width(60.dp)
                                    .height(11.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .shimmerEffect(),
                            )
                        }
                        Box(
                            Modifier
                                .width(50.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .shimmerEffect(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapChipRowSkeleton(count: Int = 5, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val chipStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
    )
    val chipLabels = listOf("Берёза", "Ольха", "Злаки", "Полынь", "Амброзия")
    val chipWidths = remember(chipStyle) {
        chipLabels.map { label ->
            val result = textMeasurer.measure(label, chipStyle)
            with(density) { result.size.width.toDp() + 22.dp }
        }
    }
    val chipHeight = remember(chipStyle) {
        val result = textMeasurer.measure("X", chipStyle)
        with(density) { result.size.height.toDp() + 10.dp }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        repeat(count.coerceAtMost(chipWidths.size)) { i ->
            Box(
                Modifier
                    .width(chipWidths[i])
                    .height(chipHeight)
                    .clip(RoundedCornerShape(12.dp))
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
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(Modifier.size(32.dp).clip(CircleShape).shimmerEffect())
            Box(
                Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Box(Modifier.size(32.dp).clip(CircleShape).shimmerEffect())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
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
