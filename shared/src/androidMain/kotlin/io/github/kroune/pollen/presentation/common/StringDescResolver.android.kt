package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.desc.StringDesc

@Composable
actual fun rememberStringDescResolver(): (StringDesc) -> String {
    val context = LocalContext.current
    return remember(context) { { desc -> desc.toString(context) } }
}
