package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel

/** Backend the recorded locations are uploaded to, see [FirebaseLocationDataSource]. */
interface LocationRemoteDataSource {
    suspend fun save(location: LocationModel)
}
