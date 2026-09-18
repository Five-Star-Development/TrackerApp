package dev.five_star.trackingapp.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dev.five_star.trackingapp.BuildConfig
import dev.five_star.trackingapp.domain.model.LocationModel
import dev.five_star.trackingapp.domain.repository.LocationRepository
import java.util.Date

class FirebaseLocationRepository: LocationRepository {

    private val TAG = "FirebaseLocationRepo"
    val database = Firebase.database(BuildConfig.FIREBASE_DATABASE_URL)
    val locationsRef = database.getReference("locations")

    override suspend fun save(location: LocationModel) {
        locationsRef.child("AddFirebaseAuthUID").push().setValue(location)
        Log.d(TAG, "onLocationResult.latitude: ${location.latitude}")
        Log.d(TAG, "onLocationResult.longitude: ${location.longitude}")
        Log.d(TAG, "onLocationResult.time: ${Date(location.time)}")
    }
}