package io.github.kroune.pollen.presentation.friends

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kroune.pollen.qr.QrScanResult

@Composable
actual fun QrScanTabContent(
    onScanResult: (QrScanResult) -> Unit,
    modifier: Modifier,
) {
    Text("QR scanning (JVM stub)")
}
