package io.github.kroune.pollen.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * Like `runCatching`, but rethrows [CancellationException] so coroutine cancellation isn't swallowed.
 * Use everywhere a `try { ... } catch (e: CancellationException) { throw e } catch (e: Exception) { ... }`
 * pattern would otherwise appear.
 */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
