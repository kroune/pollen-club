package io.github.kroune.pollen.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kroune.pollen.presentation.theme.PollenTheme

@Preview
@Composable
private fun PreviewFullScreenError() {
    PollenTheme {
        FullScreenError(onRetry = {})
    }
}

@Preview
@Composable
private fun PreviewErrorBanner() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            ErrorBanner(onRetry = {})
        }
    }
}

@Preview
@Composable
private fun PreviewLocationHeaderSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            LocationHeaderSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewWeatherCardSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            WeatherCardSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewPollenLevelCardSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            PollenLevelCardSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewPollenListSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            PollenListSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewFeedCardSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            FeedCardSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewFeedListSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            FeedListSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewMedicationCardSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            MedicationCardSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewMedicationListSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            MedicationListSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewMapChipRowSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            MapChipRowSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewMapAreaSkeleton() {
    PollenTheme {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            MapAreaSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewPersonalIndexCardSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            PersonalIndexCardSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewDayStripSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            DayStripSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewForecastDetailHeaderSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            ForecastDetailHeaderSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewForecastDetailChartSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            ForecastDetailChartSkeleton()
        }
    }
}

@Preview
@Composable
private fun PreviewForecastDetailStatsSkeleton() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            ForecastDetailStatsSkeleton()
        }
    }
}
