package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel

/** Read access to the uploaded locations for the observer, the upload itself stays with the service. */
interface LocationHistoryRepository {
    suspend fun load(id: String): List<LocationModel>
}
