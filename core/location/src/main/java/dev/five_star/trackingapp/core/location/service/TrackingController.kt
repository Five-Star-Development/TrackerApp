package dev.five_star.trackingapp.core.location.service

interface TrackingController {
    /** Must be called while the app is in the foreground and location permission is granted. */
    fun start()
    fun stop()
}
