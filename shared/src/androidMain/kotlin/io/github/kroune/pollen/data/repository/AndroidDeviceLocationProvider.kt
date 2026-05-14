package io.github.kroune.pollen.data.repository

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import io.github.kroune.pollen.domain.model.DeviceCoordinates
import io.github.kroune.pollen.domain.model.LocationAvailability
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidDeviceLocationProvider(
    private val context: Context,
) : DeviceLocationProvider {

    private val logger = Logger.withTag("DeviceLocation")
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _availability = MutableStateFlow(checkAvailability())
    override val availability: StateFlow<LocationAvailability> = _availability.asStateFlow()

    init {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                _availability.value = checkAvailability()
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun refreshAvailability() {
        _availability.value = checkAvailability()
    }

    override suspend fun getCurrentLocation(): DeviceCoordinates? {
        if (_availability.value != LocationAvailability.Available) return null
        return try {
            getFromFused() ?: getLastKnown()
        } catch (e: SecurityException) {
            logger.w(e) { "SecurityException getting location" }
            null
        }
    }

    private suspend fun getFromFused(): DeviceCoordinates? = suspendCancellableCoroutine { cont ->
        val tokenSource = CancellationTokenSource()
        cont.invokeOnCancellation { tokenSource.cancel() }
        try {
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                tokenSource.token,
            ).addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(DeviceCoordinates(location.latitude, location.longitude))
                } else {
                    cont.resume(null)
                }
            }.addOnFailureListener { e ->
                logger.w(e) { "getCurrentLocation failed" }
                cont.resume(null)
            }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }

    private suspend fun getLastKnown(): DeviceCoordinates? = suspendCancellableCoroutine { cont ->
        try {
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    cont.resume(location?.let { DeviceCoordinates(it.latitude, it.longitude) })
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }

    private fun checkAvailability(): LocationAvailability {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFinePermission && !hasCoarsePermission) return LocationAvailability.PermissionDenied
        if (!locationManager.isLocationEnabled) return LocationAvailability.LocationDisabled
        return LocationAvailability.Available
    }
}
