package io.github.kroune.pollen.presentation.feed

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.repository.FeedRepository
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.coroutines.flow.drop
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
    localeProvider: LocaleProvider,
) : MviViewModel<FeedUiState, FeedIntent, UiEvent>(FeedUiState()) {

    init {
        onIntent(FeedIntent.Refresh)
        observeLocaleChanges(localeProvider)
    }

    private fun observeLocaleChanges(localeProvider: LocaleProvider) {
        viewModelScope.launch {
            localeProvider.currentLocale.drop(1).collect {
                onIntent(FeedIntent.Refresh)
            }
        }
    }

    override fun handleIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        val isFirstLoad = currentState.feed !is LoadState.Loaded
        if (isFirstLoad) {
            updateState { copy(feed = LoadState.Loading) }
        } else {
            updateState { copy(isRefreshing = true) }
        }
        viewModelScope.launch {
            try {
                runCatchingCancellable {
                    val data = feedRepository.getFeed()
                    updateState { copy(feed = LoadState.Loaded(data)) }
                }.onFailure {
                    updateState { copy(feed = LoadState.Failed) }
                    emitEffect(UiEvent.ShowError(MR.strings.error_load_feed.desc()))
                }
            } finally {
                updateState { copy(isRefreshing = false) }
            }
        }
    }
}
