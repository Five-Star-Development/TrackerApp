package dev.five_star.trackingapp.features.tracker.presentation

import com.google.android.gms.maps.model.LatLng

data class TrackerState(
    val gpsStrength: GpsStrength = GpsStrength.NO_SIGNAL,
    val isTracking: Boolean = false,
    val position: LatLng? = null,
)
