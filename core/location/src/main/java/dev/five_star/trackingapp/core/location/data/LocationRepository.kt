package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel

/** Uploads recorded locations; whether a location is uploaded at all is decided by the service. */
class LocationRepository(
    private val remoteDataSource: LocationRemoteDataSource
) {
    suspend fun upload(location: LocationModel) {
        remoteDataSource.save(location)
    }
}
