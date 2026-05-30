package io.github.kroune.pollen.presentation.feed

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.repository.FeedRepository
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Stable
data class FeedUiState(
    val feed: LoadState<FeedDataDomain> = LoadState.Loading,
    val isRefreshing: Boolean = false,
)

sealed interface FeedIntent {
    data object Refresh : FeedIntent
}

class FeedViewModel(
    private val feedRepository: FeedRepository,
) : MviViewModel<FeedUiState, FeedIntent, UiEvent>(FeedUiState()) {

    private var feedJob: Job? = null

    init {
        feedJob = launchFeed()
    }

    override fun handleIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Refresh -> {
                updateState { copy(isRefreshing = true) }
                feedJob?.cancel()
                feedJob = launchFeed()
            }
        }
    }

    private fun launchFeed(): Job = viewModelScope.launch {
        feedRepository.getFeed()
            .catch {
                updateState {
                    copy(
                        feed = if (feed is LoadState.Loaded) feed else LoadState.Failed,
                        isRefreshing = false,
                    )
                }
                emitEffect(UiEvent.ShowError(MR.strings.error_load_feed.desc()))
            }
            .collect { data ->
                updateState { copy(feed = LoadState.Loaded(data), isRefreshing = false) }
            }
    }
}
