package io.github.kroune.pollen.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual fun observeSystemTimeChanges(): Flow<Unit> = emptyFlow()