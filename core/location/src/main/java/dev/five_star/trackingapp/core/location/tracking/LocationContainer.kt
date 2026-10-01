package dev.five_star.trackingapp.core.location.tracking

import android.content.Context
import dev.five_star.trackingapp.core.location.controller.LocationTrackingController
import dev.five_star.trackingapp.core.location.data.LocationDataSource
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository

/**
 * Holds the app wide location dependencies. Created once by the Application,
 * which exposes it through [LocationContainerProvider].
 */
class LocationContainer(
    context: Context,
    val repositories: List<LocationRepository>
) {
    private val appContext = context.applicationContext

    val trackingStatus = TrackingStatus(appContext)
    val locationDataSource = LocationDataSource(appContext)
    val trackingController = LocationTrackingController(appContext)
}

/**
 * Implemented by the Application so the service can reach the [LocationContainer]
 * without depending on the app module.
 */
interface LocationContainerProvider {
    val locationContainer: LocationContainer
}
