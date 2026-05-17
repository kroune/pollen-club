package io.github.kroune.pollen.qr

sealed interface QrScanResult {
    data class Success(val value: String) : QrScanResult
    data object Cancelled : QrScanResult
    data class Error(val message: String) : QrScanResult
}
