package dev.five_star.trackingapp.core.location.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import dev.five_star.trackingapp.core.location.model.LocationModel
import kotlinx.coroutines.tasks.await

class FirebaseLocationDataSource(database: FirebaseDatabase) : LocationRemoteDataSource {

    private val locationsRef = database.getReference("locations")

    override suspend fun save(location: LocationModel) {
        locationsRef.child("AddFirebaseAuthUID").push().setValue(location)
    }

    override suspend fun load(id: String): List<LocationModel> {
        val snapshot = locationsRef.child(id).get().await()
        return snapshot.children.mapNotNull { it.toLocationModel() }
    }

    // mapped by hand because getValue<LocationModel>() needs a no-arg constructor and mutable fields;
    // Firebase returns whole numbers as Long, so every field is read as Number
    private fun DataSnapshot.toLocationModel(): LocationModel? {
        fun number(key: String) = child(key).value as? Number
        return LocationModel(
            latitude = number("latitude")?.toDouble() ?: return null,
            longitude = number("longitude")?.toDouble() ?: return null,
            accuracy = number("accuracy")?.toFloat() ?: return null,
            time = number("time")?.toLong() ?: return null
        )
    }
}
