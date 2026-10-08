package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel

/**
 * Handles every new location: it is always published as the latest location,
 * but only uploaded while upload is enabled.
 */
class LocationRepository(
    private val trackingStatus: MutableTrackingStatus,
    private val remoteDataSource: LocationRemoteDataSource
) {
    suspend fun record(location: LocationModel) {
        trackingStatus.updateLocation(location)
        if (trackingStatus.uploadEnabled.value) {
            remoteDataSource.save(location)
        }
    }
}
