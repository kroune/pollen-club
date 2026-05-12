package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.desc.StringDesc

@Composable
expect fun rememberStringDescResolver(): (StringDesc) -> String
