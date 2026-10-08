package dev.five_star.trackingapp.feature.tracker

import com.google.android.gms.maps.model.LatLng

data class TrackerState(
    val gpsStrength: GpsStrength = GpsStrength.NO_SIGNAL,
    val isUploading: Boolean = false,
    val location: LatLng? = null,
    val zoom: Float = DEFAULT_ZOOM
)

const val DEFAULT_ZOOM = 15f
