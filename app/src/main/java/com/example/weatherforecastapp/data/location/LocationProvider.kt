package com.example.weatherforecastapp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Provides user's current location using FusedLocationProviderClient
 */
class LocationProvider @Inject constructor(
    @ApplicationContext
    private val mContext: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(mContext)

    /**
     * Gets current location of the device
     * Requires location permissions to be granted
     *
     * @return Result containing Location or error
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Location> {
        if (!hasLocationPermission()) {
            return Result.failure(LocationException("Location permission not granted."))
        }
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location: Location? = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                Result.success(location)
            } else {
                Result.failure(LocationException("Unable to get current location."))
            }
        } catch (e: Exception) {

            Result.failure(LocationException(e.message ?: "An unknown error occurred while fetching location."))
        }
    }

    /**
     * Checks if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Custom exception for location-related errors
 */
class LocationException(message: String) : Exception(message)
