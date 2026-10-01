package dev.five_star.trackingapp.core.location.data

import com.google.firebase.Firebase
import com.google.firebase.database.database
import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository

class FirebaseLocationRepository(databaseUrl: String) : LocationRepository {

    val database = Firebase.database(databaseUrl)
    val locationsRef = database.getReference("locations")

    override suspend fun save(location: LocationModel) {
        locationsRef.child("AddFirebaseAuthUID").push().setValue(location)
    }
}
