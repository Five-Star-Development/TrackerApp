package dev.five_star.trackingapp.core.location.controller

import dev.five_star.trackingapp.core.location.data.toDomain
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository
import dev.five_star.trackingapp.core.location.service.LocationEventBus
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
                val domainLocation = location.toDomain()
                repository.forEach { it.save(domainLocation) }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
