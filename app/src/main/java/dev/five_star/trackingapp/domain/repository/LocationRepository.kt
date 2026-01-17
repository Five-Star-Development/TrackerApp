package dev.five_star.trackingapp.domain.repository

import android.location.Location

interface LocationRepository {
    suspend fun save(location: Location)
}