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

private fun midnightTicker() = flow {
    while (true) {
        val now = kotlin.time.Clock.System.now()
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val secondsUntilMidnight = (24 * 3600L) -
            (localNow.hour * 3600L + localNow.minute * 60L + localNow.second)
        delay(secondsUntilMidnight * 1000 + 100)
        emit(Unit)
    }
}
