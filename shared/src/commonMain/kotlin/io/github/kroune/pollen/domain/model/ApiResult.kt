package io.github.kroune.pollen.domain.model

import co.touchlab.kermit.Logger

private val defaultLogger = Logger.withTag("safeApiCall")

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(
    logger: Logger = defaultLogger,
    description: String = "api call",
    block: suspend () -> T,
): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: kotlin.coroutines.cancellation.CancellationException) {
    throw e
} catch (e: Exception) {
    logger.e(e) { "Failed to $description" }
    ApiResult.Error(e.message ?: "Unknown error", e)
}
