package io.github.kroune.pollen.presentation.friends

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.kroune.pollen.qr.QrScanResult

@Composable
actual fun QrScanTabContent(
    onScanResult: (QrScanResult) -> Unit,
    modifier: Modifier,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text("QR scanning not available on this platform")
    }
}
