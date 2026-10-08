package dev.five_star.trackingapp.core.location.data

import com.google.firebase.database.FirebaseDatabase
import dev.five_star.trackingapp.core.location.model.LocationModel

class FirebaseLocationDataSource(database: FirebaseDatabase) : LocationRemoteDataSource {

    private val locationsRef = database.getReference("locations")

    override suspend fun save(location: LocationModel) {
        locationsRef.child("AddFirebaseAuthUID").push().setValue(location)
    }
}
