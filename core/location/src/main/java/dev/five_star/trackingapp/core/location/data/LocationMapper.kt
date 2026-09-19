package dev.five_star.trackingapp.core.location.data

import android.location.Location
import dev.five_star.trackingapp.core.location.domain.model.LocationModel

fun Location.toDomain(): LocationModel {
    return LocationModel(
        latitude = latitude,
        longitude = longitude,
        time = time
    )
}
