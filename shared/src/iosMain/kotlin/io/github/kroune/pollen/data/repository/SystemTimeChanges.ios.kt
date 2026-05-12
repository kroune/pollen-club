package io.github.kroune.pollen.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSystemTimeZoneDidChangeNotification
import platform.Foundation.NSOperationQueue

actual fun observeSystemTimeChanges(): Flow<Unit> = callbackFlow {
    val observer = NSNotificationCenter.defaultCenter.addObserverForName(
        name = NSSystemTimeZoneDidChangeNotification,
        `object` = null,
        queue = NSOperationQueue.mainQueue,
    ) {
        trySend(Unit)
    }
    awaitClose {
        NSNotificationCenter.defaultCenter.removeObserver(observer)
    }
}
