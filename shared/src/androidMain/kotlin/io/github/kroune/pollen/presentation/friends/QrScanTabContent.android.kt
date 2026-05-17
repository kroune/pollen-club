package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.qr.QrScanResult
import io.github.kroune.pollen.qr.QrScannerUi
import io.github.kroune.pollen.qr.ScannerStrings
import org.koin.compose.koinInject

@Composable
actual fun QrScanTabContent(
    onScanResult: (QrScanResult) -> Unit,
    modifier: Modifier,
) {
    val scanner = koinInject<QrScannerUi>()
    scanner.Content(
        onResult = onScanResult,
        strings = ScannerStrings(
            scanHint = stringResource(MR.strings.friends_qr_scan_hint),
            cameraPermissionRationale = stringResource(MR.strings.error_camera_permission),
            allowCameraButton = stringResource(MR.strings.friends_allow_camera),
        ),
        modifier = modifier,
    )
}
