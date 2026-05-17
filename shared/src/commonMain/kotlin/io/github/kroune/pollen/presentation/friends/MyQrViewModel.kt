package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
data class MyQrUiState(
    val myServerId: LoadState<String> = LoadState.Loading,
)

class MyQrViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MyQrUiState())
    val state: StateFlow<MyQrUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(myServerId = LoadState.Loading) }
            try {
                val user = userRepository.getLocalUser()
                val serverId = user?.serverId?.takeIf { it > 0 }?.toString() ?: ""
                _state.update { it.copy(myServerId = LoadState.Loaded(serverId)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(myServerId = LoadState.Failed) }
                _events.send(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
            }
        }
    }
}
