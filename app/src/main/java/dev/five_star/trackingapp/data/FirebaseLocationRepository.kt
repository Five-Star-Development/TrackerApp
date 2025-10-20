package dev.five_star.trackingapp.data

import android.location.Location
import android.util.Log
import dev.five_star.trackingapp.domain.repository.LocationRepository
import java.util.Date

class FirebaseLocationRepository: LocationRepository {

    private val TAG = "FirebaseLocationRepo"

    override suspend fun save(location: Location) {
        Log.d(TAG, "onLocationResult.latitude: ${location.latitude}")
        Log.d(TAG, "onLocationResult.longitude: ${location.longitude}")
        Log.d(TAG, "onLocationResult.time: ${Date(location.time)}")
    }
}