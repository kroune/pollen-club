package io.github.kroune.pollen.presentation.common

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLocationPermissionLauncher(
    onResult: (granted: Boolean) -> Unit,
): () -> Unit
