package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.qr.QrScanResult
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AddFriendTab { QR, MANUAL }

@Stable
data class AddFriendUiState(
    val friendIdInput: String = "",
    val nameInput: String = "",
    val myServerId: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val selectedTab: AddFriendTab = AddFriendTab.QR,
)

class AddFriendViewModel(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddFriendUiState())
    val state: StateFlow<AddFriendUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                val user = userRepository.getLocalUser()
                val serverId = user?.serverId?.takeIf { it > 0 }?.toString() ?: ""
                _state.update { it.copy(myServerId = serverId) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
            }
        }
    }

    fun onTabSelected(tab: AddFriendTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun onQrScanned(result: QrScanResult) {
        when (result) {
            is QrScanResult.Success -> {
                val scannedId = result.value.filter { it.isDigit() }.take(MAX_FRIEND_ID_LENGTH)
                if (scannedId.isNotBlank()) {
                    _state.update {
                        it.copy(friendIdInput = scannedId, selectedTab = AddFriendTab.MANUAL)
                    }
                }
            }
            is QrScanResult.Error -> {
                viewModelScope.launch {
                    _events.send(UiEvent.ShowError(MR.strings.error_qr_scan.desc()))
                }
            }
            is QrScanResult.Cancelled -> { /* no-op */ }
        }
    }

    fun onFriendIdChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(MAX_FRIEND_ID_LENGTH)
        _state.update { it.copy(friendIdInput = filtered) }
    }

    fun onNameChanged(value: String) {
        _state.update { it.copy(nameInput = value) }
    }

    fun submit() {
        val friendId = _state.value.friendIdInput.toIntOrNull() ?: return
        val name = _state.value.nameInput.trim()

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                val user = userRepository.getLocalUser()
                val userId = user?.serverId ?: 0
                val result = friendsRepository.addFriend(userId, friendId, name)
                if (result is ApiResult.Success) {
                    _state.update { it.copy(isSubmitting = false, isSuccess = true) }
                } else {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(UiEvent.ShowError(MR.strings.error_add_friend.desc()))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(isSubmitting = false) }
                _events.send(UiEvent.ShowError(MR.strings.error_add_friend.desc()))
            }
        }
    }

    companion object {
        private const val MAX_FRIEND_ID_LENGTH = 10
    }
}
