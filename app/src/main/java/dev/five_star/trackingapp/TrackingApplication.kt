package dev.five_star.trackingapp

import android.app.Application
import dev.five_star.trackingapp.core.location.data.FirebaseLocationRepository
import dev.five_star.trackingapp.core.location.tracking.LocationContainer
import dev.five_star.trackingapp.core.location.tracking.LocationContainerProvider

class TrackingApplication : Application(), LocationContainerProvider {

    override val locationContainer: LocationContainer by lazy {
        LocationContainer(
            context = this,
            repositories = listOf(FirebaseLocationRepository(BuildConfig.FIREBASE_DATABASE_URL))
        )
    }
}
