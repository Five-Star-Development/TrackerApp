package dev.five_star.trackingapp.data

import android.location.Location
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

    override suspend fun save(location: Location) {
        val myLocation = LocationModel(location.latitude, location.longitude, location.time)
        locationsRef.child("AddFirebaseAuthUID").push().setValue(myLocation)
        Log.d(TAG, "onLocationResult.latitude: ${location.latitude}")
        Log.d(TAG, "onLocationResult.longitude: ${location.longitude}")
        Log.d(TAG, "onLocationResult.time: ${Date(location.time)}")
    }
}