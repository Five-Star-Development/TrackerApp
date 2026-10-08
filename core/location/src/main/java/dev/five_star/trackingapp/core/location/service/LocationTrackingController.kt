package dev.five_star.trackingapp.core.location.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class LocationTrackingController(context: Context) : TrackingController {

    private val context = context.applicationContext

    override fun start() {
        ContextCompat.startForegroundService(context, Intent(context, LocationService::class.java))
    }

    override fun stop() {
        context.stopService(Intent(context, LocationService::class.java))
    }
}
