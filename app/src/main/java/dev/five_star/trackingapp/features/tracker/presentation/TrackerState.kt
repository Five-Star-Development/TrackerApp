package dev.five_star.trackingapp.features.tracker.presentation

import com.google.android.gms.maps.model.LatLng

data class TrackerState(
    val gpsStrength: GpsStrength = GpsStrength.NO_SIGNAL,
    val gpsValue: Double = 0.0,
    val isTracking: Boolean = false,
    val location: LatLng? = null,
    val zoom: Float = 0f
)
