package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import co.touchlab.kermit.Logger
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.serverIdOrNull
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.qr.QrScanResult
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.coroutines.launch

private val logger = Logger.withTag("AddFriendVM")

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

sealed interface AddFriendIntent {
    data class TabSelected(val tab: AddFriendTab) : AddFriendIntent
    data class QrScanned(val result: QrScanResult) : AddFriendIntent
    data class FriendIdChanged(val value: String) : AddFriendIntent
    data class NameChanged(val value: String) : AddFriendIntent
    data object Submit : AddFriendIntent
}

class AddFriendViewModel(
    private val friendsRepository: FriendsRepository,
    private val userSession: UserSession,
) : MviViewModel<AddFriendUiState, AddFriendIntent, UiEvent>(AddFriendUiState()) {

    init {
        viewModelScope.launch {
            runCatchingCancellable {
                val serverId = userSession.currentUser().identity.serverIdOrNull
                updateState { copy(myServerId = serverId?.toString() ?: "") }
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
            }
        }
    }

    override fun handleIntent(intent: AddFriendIntent) {
        when (intent) {
            is AddFriendIntent.TabSelected -> updateState { copy(selectedTab = intent.tab) }
            is AddFriendIntent.QrScanned -> handleQrScanned(intent.result)
            is AddFriendIntent.FriendIdChanged -> {
                val filtered = intent.value.filter { it.isDigit() }.take(MAX_FRIEND_ID_LENGTH)
                updateState { copy(friendIdInput = filtered) }
            }
            is AddFriendIntent.NameChanged -> updateState { copy(nameInput = intent.value) }
            AddFriendIntent.Submit -> submit()
        }
    }

    private fun handleQrScanned(result: QrScanResult) {
        when (result) {
            is QrScanResult.Success -> {
                val scannedId = result.value.filter { it.isDigit() }.take(MAX_FRIEND_ID_LENGTH)
                if (scannedId.isNotBlank()) {
                    updateState { copy(friendIdInput = scannedId, selectedTab = AddFriendTab.MANUAL) }
                }
            }
            is QrScanResult.Error -> {
                logger.w { "QR scan error: ${result.message}" }
                emitEffect(UiEvent.ShowError(MR.strings.error_qr_scan.desc()))
            }
            is QrScanResult.Cancelled -> { /* no-op */ }
        }
    }

    private fun submit() {
        val friendId = currentState.friendIdInput.toIntOrNull() ?: return
        val name = currentState.nameInput.trim()

        viewModelScope.launch {
            updateState { copy(isSubmitting = true) }
            runCatchingCancellable {
                friendsRepository.addFriend(friendId, name)
            }.onSuccess { result ->
                if (result is ApiResult.Success) {
                    updateState { copy(isSubmitting = false, isSuccess = true) }
                } else {
                    updateState { copy(isSubmitting = false) }
                    emitEffect(UiEvent.ShowError(MR.strings.error_add_friend.desc()))
                }
            }.onFailure {
                updateState { copy(isSubmitting = false) }
                emitEffect(UiEvent.ShowError(MR.strings.error_add_friend.desc()))
            }
        }
    }

    companion object {
        private const val MAX_FRIEND_ID_LENGTH = 10
    }
}
