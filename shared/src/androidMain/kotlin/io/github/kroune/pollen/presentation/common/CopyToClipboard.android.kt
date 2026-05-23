package io.github.kroune.pollen.presentation.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberCopyToClipboard(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { text: String ->
            val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            manager.setPrimaryClip(ClipData.newPlainText(null, text))
        }
    }
}
