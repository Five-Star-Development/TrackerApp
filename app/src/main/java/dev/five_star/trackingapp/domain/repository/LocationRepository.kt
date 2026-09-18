package dev.five_star.trackingapp.domain.repository

import dev.five_star.trackingapp.domain.model.LocationModel

interface LocationRepository {
    suspend fun save(location: LocationModel)
}