package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.domain.model.TodayProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TodayProviderImpl(
    scope: CoroutineScope,
) : TodayProvider {

    override val today: StateFlow<LocalDate> =
        merge(midnightTicker(), observeSystemTimeChanges())
            .map { currentLocalDate() }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, currentLocalDate())
}

private fun currentLocalDate(): LocalDate =
    kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

private const val SECONDS_PER_DAY = 86_400L
private const val SECONDS_PER_HOUR = 3_600L
private const val SECONDS_PER_MINUTE = 60L
private const val MIDNIGHT_BUFFER_MS = 100L

private fun midnightTicker() = flow {
    while (true) {
        val now = kotlin.time.Clock.System.now()
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val secondsUntilMidnight = SECONDS_PER_DAY -
            (localNow.hour * SECONDS_PER_HOUR + localNow.minute * SECONDS_PER_MINUTE + localNow.second)
        delay(secondsUntilMidnight * 1000 + MIDNIGHT_BUFFER_MS)
        emit(Unit)
    }
}
