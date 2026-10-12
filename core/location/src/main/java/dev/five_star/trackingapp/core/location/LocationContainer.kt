package dev.five_star.trackingapp.core.location

import android.content.Context
import dev.five_star.trackingapp.core.location.data.InMemoryTrackingStatus
import dev.five_star.trackingapp.core.location.data.LocationDataSource
import dev.five_star.trackingapp.core.location.data.LocationRemoteDataSource
import dev.five_star.trackingapp.core.location.data.LocationRepository
import dev.five_star.trackingapp.core.location.data.MutableTrackingStatus
import dev.five_star.trackingapp.core.location.data.TrackingStatus
import dev.five_star.trackingapp.core.location.service.LocationTrackingController
import dev.five_star.trackingapp.core.location.service.TrackingController

/**
 * Holds the app wide location dependencies. Created once by the Application,
 * which exposes it through [LocationContainerProvider].
 */
class LocationContainer(
    context: Context,
    remoteDataSource: LocationRemoteDataSource
) {
    private val appContext = context.applicationContext

    // the service writes the status, everyone else only gets the read-only interface
    internal val mutableTrackingStatus: MutableTrackingStatus = InMemoryTrackingStatus()
    val trackingStatus: TrackingStatus get() = mutableTrackingStatus

    internal val locationRepository = LocationRepository(remoteDataSource)

    val locationDataSource = LocationDataSource(appContext)
    val trackingController: TrackingController = LocationTrackingController(appContext)
}

/**
 * Implemented by the Application so the service can reach the [LocationContainer]
 * without depending on the app module.
 */
interface LocationContainerProvider {
    val locationContainer: LocationContainer
}
