package dev.five_star.trackingapp.core.location.controller

import android.content.Context
import android.content.Intent
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository
import dev.five_star.trackingapp.core.location.service.LocationService

class LocationTrackingController(
    private val context: Context,
    private val repository: LocationRepository
) {

    fun start() {
        context.startService(Intent(context, LocationService::class.java))
        LocationControllerManager.start(repository)
    }

    fun stop() {
        context.stopService(Intent(context, LocationService::class.java))
        LocationControllerManager.stop()
    }
}
