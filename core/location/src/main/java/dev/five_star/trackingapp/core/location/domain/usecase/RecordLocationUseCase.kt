package dev.five_star.trackingapp.core.location.domain.usecase

import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository
import dev.five_star.trackingapp.core.location.tracking.MutableTrackingStatus

/**
 * Handles every new location: it is always published as the latest location,
 * but only stored in the repositories while upload is enabled.
 */
class RecordLocationUseCase(
    private val trackingStatus: MutableTrackingStatus,
    private val repositories: List<LocationRepository>
) {
    suspend operator fun invoke(location: LocationModel) {
        trackingStatus.updateLocation(location)
        if (trackingStatus.uploadEnabled.value) {
            repositories.forEach { it.save(location) }
        }
    }
}
