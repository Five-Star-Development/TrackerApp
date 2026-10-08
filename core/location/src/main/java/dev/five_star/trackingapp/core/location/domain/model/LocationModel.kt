package dev.five_star.trackingapp.core.location.domain.model

data class LocationModel(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val time: Long
)
