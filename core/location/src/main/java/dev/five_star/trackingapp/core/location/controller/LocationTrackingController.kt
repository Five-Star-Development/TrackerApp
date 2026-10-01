package dev.five_star.trackingapp.core.location.controller

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dev.five_star.trackingapp.core.location.service.LocationService

class LocationTrackingController(context: Context) {

    private val context = context.applicationContext

    /** Must be called while the app is in the foreground and location permission is granted. */
    fun start() {
        ContextCompat.startForegroundService(context, Intent(context, LocationService::class.java))
    }

    fun stop() {
        context.stopService(Intent(context, LocationService::class.java))
    }
}
