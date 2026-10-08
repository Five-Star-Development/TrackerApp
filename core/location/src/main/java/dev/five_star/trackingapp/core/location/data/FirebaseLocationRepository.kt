package dev.five_star.trackingapp.core.location.data

import com.google.firebase.database.FirebaseDatabase
import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository

class FirebaseLocationRepository(database: FirebaseDatabase) : LocationRepository {

    private val locationsRef = database.getReference("locations")

    override suspend fun save(location: LocationModel) {
        locationsRef.child("AddFirebaseAuthUID").push().setValue(location)
    }
}
