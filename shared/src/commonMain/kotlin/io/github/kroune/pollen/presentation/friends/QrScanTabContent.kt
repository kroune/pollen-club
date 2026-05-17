package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kroune.pollen.qr.QrScanResult

@Composable
expect fun QrScanTabContent(
    onScanResult: (QrScanResult) -> Unit,
    modifier: Modifier = Modifier,
)
