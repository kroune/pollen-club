package io.github.kroune.pollen.presentation.friends

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.FriendLastPinDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class FriendUi(
    val friendId: Int,
    val name: String?,
    val lastPinFeeling: Feeling?,
    val lastPinPollenName: String?,
    val lastPinDate: String?,
)

@Stable
data class FriendsListUiState(
    val friends: LoadState<ImmutableList<FriendUi>> = LoadState.Loading,
    val myServerId: String = "",
)

class FriendsListViewModel(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository,
    private val pollenRepository: PollenRepository,
    localeProvider: LocaleProvider,
) : ViewModel() {

    val locale: StateFlow<AppLocale> = localeProvider.currentLocale
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLocale.RU)

    private val _state = MutableStateFlow(FriendsListUiState())
    val state: StateFlow<FriendsListUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var observeJob: Job? = null
    private val lastPins = MutableStateFlow<Map<Int, FriendLastPinDomain>>(emptyMap())

    init {
        loadData()
    }

    fun loadData() {
        observeJob?.cancel()

        viewModelScope.launch {
            try {
                val user = userRepository.getLocalUser()
                val userId = user?.serverId ?: 0
                _state.value = _state.value.copy(myServerId = if (userId > 0) userId.toString() else "")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.withTag("FriendsListVM").w(e) { "Failed to load user" }
            }
        }

        observeJob = viewModelScope.launch {
            try {
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
                    _state.value = _state.value.copy(friends = LoadState.Loaded(items))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.value = _state.value.copy(friends = LoadState.Failed)
                _events.send(UiEvent.ShowError(MR.strings.error_load_friends.desc()))
            }
        }

        viewModelScope.launch {
            try {
                val user = userRepository.getLocalUser()
                val userId = user?.serverId ?: 0

                val syncResult = friendsRepository.syncFriends(userId)
                if (syncResult is ApiResult.Error) {
                    Logger.withTag("FriendsListVM").w { "Failed to sync friends" }
                }

                val pinsResult = friendsRepository.getLastPinsForFriends(userId)
                if (pinsResult is ApiResult.Success) {
                    lastPins.value = pinsResult.data
                } else {
                    Logger.withTag("FriendsListVM").w { "Failed to load friend pins" }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.withTag("FriendsListVM").w(e) { "Failed to sync" }
            }
        }
    }

    fun deleteFriend(friendId: Int) {
        viewModelScope.launch {
            try {
                val user = userRepository.getLocalUser()
                val result = friendsRepository.deleteFriend(user?.serverId ?: 0, friendId)
                if (result is ApiResult.Error) {
                    _events.send(UiEvent.ShowError(MR.strings.error_delete_friend.desc()))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.send(UiEvent.ShowError(MR.strings.error_delete_friend.desc()))
            }
        }
    }
}
