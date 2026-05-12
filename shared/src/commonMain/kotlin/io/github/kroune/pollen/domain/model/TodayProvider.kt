package io.github.kroune.pollen.domain.model

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface TodayProvider {
    val today: StateFlow<LocalDate>
}
