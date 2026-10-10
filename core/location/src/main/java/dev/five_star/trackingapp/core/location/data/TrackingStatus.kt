package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Runtime state that [dev.five_star.trackingapp.core.location.service.LocationService] publishes for the UI.
 * Nothing is persisted, user settings like the upload live in the settings.
 * There must only be one instance per app, see [dev.five_star.trackingapp.core.location.LocationContainer].
 */
interface TrackingStatus {
    /** Last known location; kept when the service stops or the signal is lost. */
    val lastLocation: StateFlow<LocationModel?>

    /** True while the service is running and the fused provider can determine a location. */
    val isLocationAvailable: StateFlow<Boolean>
}

/** Write access for the service, the UI only gets [TrackingStatus]. */
interface MutableTrackingStatus : TrackingStatus {
    fun updateLocation(location: LocationModel)
    fun setLocationAvailable(available: Boolean)
}

class InMemoryTrackingStatus : MutableTrackingStatus {

    private val _lastLocation = MutableStateFlow<LocationModel?>(null)
    override val lastLocation: StateFlow<LocationModel?> = _lastLocation.asStateFlow()

    private val _isLocationAvailable = MutableStateFlow(false)
    override val isLocationAvailable: StateFlow<Boolean> = _isLocationAvailable.asStateFlow()

    override fun updateLocation(location: LocationModel) {
        _lastLocation.value = location
        _isLocationAvailable.value = true
    }

    override fun setLocationAvailable(available: Boolean) {
        _isLocationAvailable.value = available
    }
}
