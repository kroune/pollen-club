package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.FriendLastPinDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.model.serverIdOrNull
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

private val logger = Logger.withTag("FriendsListVM")

@Immutable
data class FriendUi(
    val friendId: Int,
    val name: String?,
    val lastPinFeeling: Feeling?,
    val lastPinPollenName: String?,
    val lastPinDate: LocalDate?,
)

@Stable
data class FriendsListUiState(
    val friends: LoadState<ImmutableList<FriendUi>> = LoadState.Loading,
    val myServerId: String = "",
)

sealed interface FriendsListIntent {
    data object LoadData : FriendsListIntent
    data class DeleteFriend(val friendId: Int) : FriendsListIntent
}

class FriendsListViewModel(
    private val friendsRepository: FriendsRepository,
    private val userSession: UserSession,
    private val pollenRepository: PollenRepository,
) : MviViewModel<FriendsListUiState, FriendsListIntent, UiEvent>(FriendsListUiState()) {

    private var observeJob: Job? = null
    private var syncJob: Job? = null
    private val lastPins = MutableStateFlow<Map<Int, FriendLastPinDomain>>(emptyMap())

    init {
        onIntent(FriendsListIntent.LoadData)
    }

    override fun handleIntent(intent: FriendsListIntent) {
        when (intent) {
            FriendsListIntent.LoadData -> loadData()
            is FriendsListIntent.DeleteFriend -> deleteFriend(intent.friendId)
        }
    }

    private fun loadData() {
        observeJob?.cancel()
        syncJob?.cancel()

        viewModelScope.launch {
            runCatchingCancellable {
                val serverId = userSession.currentUser().identity.serverIdOrNull
                updateState { copy(myServerId = serverId?.toString() ?: "") }
            }.onFailure { logger.w(it) { "Failed to load user" } }
        }

        observeJob = viewModelScope.launch {
            combine(
                friendsRepository.observeFriends(),
                pollenRepository.observePollens(),
                lastPins,
            ) { friends, pollens, pins ->
                val pollenNames = pollens.associate { it.id to it.name }
                friends.map { friend ->
                    val pin = pins[friend.friendId]
                    FriendUi(
                        friendId = friend.friendId,
                        name = friend.name.ifBlank { null },
                        lastPinFeeling = pin?.feeling,
                        lastPinPollenName = pin?.let { pollenNames[it.pollenType] },
                        lastPinDate = pin?.date,
                    )
                }.toImmutableList()
            }.collect { items ->
                updateState { copy(friends = LoadState.Loaded(items)) }
            }
        }

        syncJob = viewModelScope.launch {
            runCatchingCancellable {
                val syncResult = friendsRepository.syncFriends()
                if (syncResult is ApiResult.Error) {
                    logger.w { "Failed to sync friends" }
                    if (currentState.friends is LoadState.Loading) {
                        updateState { copy(friends = LoadState.Failed) }
                        emitEffect(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
                    }
                }

                val pinsResult = friendsRepository.getLastPinsForFriends()
                if (pinsResult is ApiResult.Success) {
                    lastPins.value = pinsResult.data
                } else {
                    logger.w { "Failed to load friend pins" }
                }
            }.onFailure {
                logger.w(it) { "Failed to sync" }
                if (currentState.friends is LoadState.Loading) {
                    updateState { copy(friends = LoadState.Failed) }
                    emitEffect(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
                }
            }
        }
    }

    private fun deleteFriend(friendId: Int) {
        viewModelScope.launch {
            runCatchingCancellable {
                val result = friendsRepository.deleteFriend(friendId)
                if (result is ApiResult.Error) {
                    emitEffect(UiEvent.ShowError(MR.strings.error_delete_friend.desc()))
                }
            }.onFailure {
                emitEffect(UiEvent.ShowError(MR.strings.error_delete_friend.desc()))
            }
        }
    }
}
