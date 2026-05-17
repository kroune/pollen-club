package io.github.kroune.pollen.qr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
data class ScannerStrings(
    val scanHint: String,
    val cameraPermissionRationale: String,
    val allowCameraButton: String,
)

interface QrScannerUi {
    @Composable
    fun Content(
        onResult: (QrScanResult) -> Unit,
        strings: ScannerStrings,
        modifier: Modifier,
    )
}
