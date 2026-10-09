package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel

/** Uploads recorded locations, but only while upload is enabled. */
class LocationRepository(
    private val trackingStatus: TrackingStatus,
    private val remoteDataSource: LocationRemoteDataSource
) {
    suspend fun upload(location: LocationModel) {
        if (trackingStatus.uploadEnabled.value) {
            remoteDataSource.save(location)
        }
    }
}
