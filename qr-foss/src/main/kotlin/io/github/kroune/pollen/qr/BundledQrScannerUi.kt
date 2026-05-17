package io.github.kroune.pollen.qr

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.github.kroune.pollen.theme.LocalPollenColors
import java.util.concurrent.Executors

class BundledQrScannerUi : QrScannerUi {
    @Composable
    override fun Content(
        onResult: (QrScanResult) -> Unit,
        strings: ScannerStrings,
        modifier: Modifier,
    ) {
        val context = LocalContext.current
        var cameraPermissionGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED,
            )
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            cameraPermissionGranted = granted
            if (!granted) {
                onResult(QrScanResult.Error(strings.cameraPermissionRationale))
            }
        }

        LaunchedEffect(Unit) {
            if (!cameraPermissionGranted) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        if (cameraPermissionGranted) {
            CameraPreviewWithScanner(
                onResult = onResult,
                scanHintText = strings.scanHint,
                modifier = modifier,
            )
        } else {
            PermissionRationale(
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                permissionText = strings.cameraPermissionRationale,
                buttonText = strings.allowCameraButton,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CameraPreviewWithScanner(
    onResult: (QrScanResult) -> Unit,
    scanHintText: String,
    modifier: Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val colors = LocalPollenColors.current
    var hasDeliveredResult by remember { mutableStateOf(false) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    DisposableEffect(Unit) {
        onDispose {
            analysisExecutor.shutdown()
            barcodeScanner.close()
        }
    }

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
            .background(colors.surfaceDark),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                processImage(imageProxy, barcodeScanner) { value ->
                                    if (!hasDeliveredResult) {
                                        hasDeliveredResult = true
                                        onResult(QrScanResult.Success(value))
                                    }
                                }
                            }
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis,
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            onRelease = { view ->
                try {
                    ProcessCameraProvider.getInstance(view.context).get().unbindAll()
                } catch (e: Exception) {
                    co.touchlab.kermit.Logger.withTag("BundledQrScanner")
                        .w(e) { "Failed to unbind camera" }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .viewfinderOverlay(scanLineProgress, colors.accent),
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = scanHintText,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImage(
    imageProxy: ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onQrDetected: (String) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                if (barcode.format == Barcode.FORMAT_QR_CODE) {
                    barcode.rawValue?.let { onQrDetected(it) }
                }
            }
        }
        .addOnCompleteListener { imageProxy.close() }
}

@Composable
private fun PermissionRationale(
    onRequestPermission: () -> Unit,
    permissionText: String,
    buttonText: String,
    modifier: Modifier,
) {
    val colors = LocalPollenColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = permissionText,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRequestPermission) {
                Text(buttonText)
            }
        }
    }
}
