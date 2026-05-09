package io.github.kroune.pollen.presentation.feed

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.repository.FeedRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@Stable
data class FeedUiState(
    val feed: LoadState<FeedDataDomain> = LoadState.Loading,
)

class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val userRepository: UserRepository,
    localeProvider: LocaleProvider,
) : ViewModel() {

    val locale: StateFlow<AppLocale> = localeProvider.currentLocale
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppLocale.RU)

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = FeedUiState(feed = LoadState.Loading)
        viewModelScope.launch {
            try {
                val user = userRepository.getLocalUser()
                val data = feedRepository.getFeed(user?.serverId ?: 0)
                _uiState.value = FeedUiState(feed = LoadState.Loaded(data))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = FeedUiState(feed = LoadState.Failed)
                _events.send(UiEvent.ShowError(e.message ?: "Failed to load feed"))
            }
        }
    }
}
