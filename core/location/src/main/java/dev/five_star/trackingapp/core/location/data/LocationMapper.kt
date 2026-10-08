package dev.five_star.trackingapp.core.location.data

import android.location.Location
import dev.five_star.trackingapp.core.location.model.LocationModel

fun Location.toModel(): LocationModel {
    return LocationModel(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        time = time
    )
}
