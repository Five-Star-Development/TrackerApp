package dev.five_star.trackingapp.controller

import dev.five_star.trackingapp.domain.repository.LocationRepository

object LocationControllerManager {
    private var controller: LocationController? = null

    fun start(repository: LocationRepository) {
        if (controller == null) {
            controller = LocationController(repository).apply { start() }
        }
    }

    fun stop() {
        controller?.stop()
        controller = null
    }
}