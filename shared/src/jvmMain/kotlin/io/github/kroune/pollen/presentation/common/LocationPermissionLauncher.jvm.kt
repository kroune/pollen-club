package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable

@Composable
actual fun rememberLocationPermissionLauncher(
    onResult: (granted: Boolean) -> Unit,
): () -> Unit = { onResult(false) }
