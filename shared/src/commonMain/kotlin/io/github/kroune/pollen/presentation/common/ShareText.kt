package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareTextLauncher(): (String) -> Unit
