package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel

/** Uploads recorded locations; whether a location is uploaded at all is decided by the service. */
class LocationRepository(
    private val remoteDataSource: LocationRemoteDataSource
) : LocationHistoryRepository {
    suspend fun upload(location: LocationModel) {
        remoteDataSource.save(location)
    }

    override suspend fun load(id: String): List<LocationModel> {
        return remoteDataSource.load(id)
    }
}
