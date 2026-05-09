package io.github.kroune.pollen.domain.model

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Loaded<T>(val data: T) : LoadState<T>
    data object Failed : LoadState<Nothing>
}

val <T> LoadState<T>.dataOrNull: T?
    get() = (this as? LoadState.Loaded)?.data
