package dev.five_star.trackingapp.core.location.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository
import java.util.Date

class FirebaseLocationRepository(databaseUrl: String) : LocationRepository {

    private val TAG = "FirebaseLocationRepo"
    val database = Firebase.database(databaseUrl)
    val locationsRef = database.getReference("locations")

    override suspend fun save(location: LocationModel) {
        locationsRef.child("AddFirebaseAuthUID").push().setValue(location)
        Log.d(TAG, "onLocationResult.latitude: ${location.latitude}")
        Log.d(TAG, "onLocationResult.longitude: ${location.longitude}")
        Log.d(TAG, "onLocationResult.time: ${Date(location.time)}")
    }
}
