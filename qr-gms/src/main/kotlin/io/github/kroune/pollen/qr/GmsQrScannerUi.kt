package io.github.kroune.pollen.qr

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import io.github.kroune.pollen.theme.LocalPollenColors

class GmsQrScannerUi : QrScannerUi {
    @Composable
    override fun Content(
        onResult: (QrScanResult) -> Unit,
        strings: ScannerStrings,
        modifier: Modifier,
    ) {
        val context = LocalContext.current
        val colors = LocalPollenColors.current

        val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
        val scanLineProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "scanLine",
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceDark)
                .clickable {
                    val options = GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                    GmsBarcodeScanning.getClient(context, options).startScan()
                        .addOnSuccessListener { barcode ->
                            val raw = barcode.rawValue
                            if (raw != null) {
                                onResult(QrScanResult.Success(raw))
                            } else {
                                onResult(QrScanResult.Cancelled)
                            }
                        }
                        .addOnCanceledListener { onResult(QrScanResult.Cancelled) }
                        .addOnFailureListener { e ->
                            onResult(QrScanResult.Error(e.message.orEmpty()))
                        }
                }
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .viewfinderOverlay(scanLineProgress, colors.accent),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = strings.scanHint,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
