package io.github.kroune.pollen.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

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
