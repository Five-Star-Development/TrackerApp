package dev.five_star.trackingapp.core.location.tracking

import android.content.Context
import androidx.core.content.edit
import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared state between [dev.five_star.trackingapp.core.location.service.LocationService] and the UI.
 * The service publishes the latest location, the UI decides whether the service uploads it.
 * There must only be one instance per app, see [LocationContainer].
 */
interface TrackingStatus {
    /** Last known location; kept when the service stops or the signal is lost. */
    val lastLocation: StateFlow<LocationModel?>

    /** True while the service is running and the fused provider can determine a location. */
    val isLocationAvailable: StateFlow<Boolean>

    val uploadEnabled: StateFlow<Boolean>

    fun setUploadEnabled(enabled: Boolean)
}

class SharedPreferencesTrackingStatus(context: Context) : TrackingStatus {

    private val prefs = context.getSharedPreferences("tracking_status_prefs", Context.MODE_PRIVATE)

    private val _lastLocation = MutableStateFlow<LocationModel?>(null)
    override val lastLocation: StateFlow<LocationModel?> = _lastLocation.asStateFlow()

    private val _isLocationAvailable = MutableStateFlow(false)
    override val isLocationAvailable: StateFlow<Boolean> = _isLocationAvailable.asStateFlow()

    // persisted so a restarted service knows whether it should upload
    private val _uploadEnabled = MutableStateFlow(prefs.getBoolean(KEY_UPLOAD_ENABLED, false))
    override val uploadEnabled: StateFlow<Boolean> = _uploadEnabled.asStateFlow()

    override fun setUploadEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_UPLOAD_ENABLED, enabled) }
        _uploadEnabled.value = enabled
    }

    // only the service in this module publishes locations and availability
    internal fun updateLocation(location: LocationModel) {
        _lastLocation.value = location
        _isLocationAvailable.value = true
    }

    internal fun setLocationAvailable(available: Boolean) {
        _isLocationAvailable.value = available
    }

    private companion object {
        const val KEY_UPLOAD_ENABLED = "upload_enabled"
    }
}
