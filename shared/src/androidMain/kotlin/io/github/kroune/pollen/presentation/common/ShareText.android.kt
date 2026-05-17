package io.github.kroune.pollen.presentation.common

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShareTextLauncher(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { text: String ->
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, null))
        }
    }
}
