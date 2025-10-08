package dev.five_star.trackingapp.features.tracker.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationDataSource(private val context: Context) {


    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    fun getGnssSignalUpdates(): Flow<Double> = callbackFlow {
        if (!hasPermission()) {
            close(IllegalStateException("Location permission not granted"))
            return@callbackFlow
        }

        val callback = object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(status: GnssStatus) {
                var sum = 0.0
                var count = 0

                for (i in 0 until status.satelliteCount) {
                    val cn0 = status.getCn0DbHz(i)
                    if (cn0 > 0) {
                        sum += cn0
                        count++
                    }
                }

                val avgCn0 = if (count > 0) sum / count else 0.0
                Log.d("LocationDataSource", "onSatelliteStatusChanged: $avgCn0")
                trySend(avgCn0)
            }
        }

        locationManager.registerGnssStatusCallback(
            callback, android.os.Handler(context.mainLooper)
        )

        awaitClose {
            locationManager.unregisterGnssStatusCallback(callback)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        if (!hasPermission()) {
            close(IllegalStateException("Location permission not granted"))
            return@callbackFlow
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 0L
        ).setMinUpdateDistanceMeters(5F).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    Log.d("LocationDataSource", "lastLocation: $it")
                    Log.d("LocationDataSource", "accuracy: ${it.accuracy}")
                    trySend(it)
                }
            }
        }

        fusedClient.requestLocationUpdates(
            request, callback, Looper.getMainLooper()
        )

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
