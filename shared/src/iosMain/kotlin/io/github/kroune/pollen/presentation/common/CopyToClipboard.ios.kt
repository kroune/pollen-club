package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

@Composable
actual fun rememberCopyToClipboard(): (String) -> Unit = { text ->
    UIPasteboard.generalPasteboard.string = text
}
