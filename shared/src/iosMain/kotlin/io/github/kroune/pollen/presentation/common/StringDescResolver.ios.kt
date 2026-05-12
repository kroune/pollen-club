package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.icerock.moko.resources.desc.StringDesc

@Composable
actual fun rememberStringDescResolver(): (StringDesc) -> String {
    return remember { { desc -> desc.localized() } }
}
