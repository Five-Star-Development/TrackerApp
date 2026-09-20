package dev.five_star.trackingapp.core.location.domain.repository

import dev.five_star.trackingapp.core.location.domain.model.LocationModel

interface LocationRepository {
    suspend fun save(location: LocationModel)
}
