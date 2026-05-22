package io.github.kroune.pollen.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class MviViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    protected val currentState: State get() = _state.value

    fun onIntent(intent: Intent) {
        handleIntent(intent)
    }

    protected abstract fun handleIntent(intent: Intent)

    protected fun updateState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    protected fun emitEffect(effect: Effect) {
        _effects.trySend(effect)
    }
}
