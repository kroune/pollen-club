package io.github.kroune.pollen.data.repository

import kotlinx.coroutines.flow.Flow

expect fun observeSystemTimeChanges(): Flow<Unit>
