package io.github.kroune.pollen.presentation.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectEvents(
    events: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
    onRetry: (() -> Unit)? = null,
) {
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is UiEvent.ShowError -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = if (onRetry != null) "Retry" else null,
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onRetry?.invoke()
                    }
                }
            }
        }
    }
}
