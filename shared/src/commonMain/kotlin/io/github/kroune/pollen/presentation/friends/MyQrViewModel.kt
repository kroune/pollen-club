package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.coroutines.launch

@Stable
data class MyQrUiState(
    val myServerId: LoadState<String> = LoadState.Loading,
)

sealed interface MyQrIntent {
    data object LoadData : MyQrIntent
}

class MyQrViewModel(
    private val userSession: UserSession,
) : MviViewModel<MyQrUiState, MyQrIntent, UiEvent>(MyQrUiState()) {

    init {
        onIntent(MyQrIntent.LoadData)
    }

    override fun handleIntent(intent: MyQrIntent) {
        when (intent) {
            MyQrIntent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        updateState { copy(myServerId = LoadState.Loading) }
        viewModelScope.launch {
            runCatchingCancellable {
                // The QR encodes the user's own id, so ensure one exists (register if needed).
                val serverId = userSession.requireUserId()
                updateState { copy(myServerId = LoadState.Loaded(serverId.toString())) }
            }.onFailure {
                updateState { copy(myServerId = LoadState.Failed) }
                emitEffect(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
            }
        }
    }
}
