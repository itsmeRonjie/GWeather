package com.ronjie.gweather.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ronjie.gweather.domain.model.Coordinates
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private lateinit var locationCallback: LocationCallback

    val locationUpdates: Flow<Coordinates> = callbackFlow {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val coordinates = Coordinates(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    trySend(coordinates)
                }
            }
        }

        if (hasLocationPermission()) startLocationUpdatesInternal()

        awaitClose { stopLocationUpdates() }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdatesInternal() {
        if (!::locationCallback.isInitialized) {
            return
        }

        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { lastLocation ->
                lastLocation?.let { location ->
                    (locationUpdates as? MutableStateFlow)?.value = Coordinates(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            }.addOnFailureListener { e ->
                Log.e("LocationProvider", "Failed to get last location", e)
            }

            val locationRequest = LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(5000)
                .setMinUpdateDistanceMeters(10f)
                .build()

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.e("LocationProvider", "Error starting location updates", e)
        }
    }

    private fun stopLocationUpdates() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    fun initialize(activity: ComponentActivity) {
        locationPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.entries.all { it.value }) {
                if (::locationCallback.isInitialized) {
                    startLocationUpdatesInternal()
                }
            } else {
                Toast.makeText(
                    context,
                    "Location permission is required for weather updates",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
