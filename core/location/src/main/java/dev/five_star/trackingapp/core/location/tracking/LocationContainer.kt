package dev.five_star.trackingapp.core.location.tracking

import android.content.Context
import dev.five_star.trackingapp.core.location.controller.LocationTrackingController
import dev.five_star.trackingapp.core.location.controller.TrackingController
import dev.five_star.trackingapp.core.location.data.LocationDataSource
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository
import dev.five_star.trackingapp.core.location.domain.usecase.RecordLocationUseCase

/**
 * Holds the app wide location dependencies. Created once by the Application,
 * which exposes it through [LocationContainerProvider].
 */
class LocationContainer(
    context: Context,
    repositories: List<LocationRepository>
) {
    private val appContext = context.applicationContext

    // the service writes the status, everyone else only gets the read-only interface
    internal val mutableTrackingStatus: MutableTrackingStatus = SharedPreferencesTrackingStatus(appContext)
    val trackingStatus: TrackingStatus get() = mutableTrackingStatus

    internal val recordLocation = RecordLocationUseCase(mutableTrackingStatus, repositories)

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
