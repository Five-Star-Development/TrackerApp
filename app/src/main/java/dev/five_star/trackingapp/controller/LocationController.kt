package dev.five_star.trackingapp.controller

import dev.five_star.trackingapp.domain.repository.LocationRepository
import dev.five_star.trackingapp.service.LocationEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LocationController(vararg val repository: LocationRepository) {

    private var job: Job? = null

    fun start(scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {
        if (job != null) {
            return
        }
        job = scope.launch {
            LocationEventBus.location.collect { location ->
                repository.forEach { it.save(location) }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}