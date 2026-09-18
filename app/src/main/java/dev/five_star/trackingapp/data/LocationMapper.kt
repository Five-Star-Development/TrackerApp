package dev.five_star.trackingapp.data

import android.location.Location
import dev.five_star.trackingapp.domain.model.LocationModel

fun Location.toDomain(): LocationModel {
    return LocationModel(
        latitude = latitude,
        longitude = longitude,
        time = time
    )
}
