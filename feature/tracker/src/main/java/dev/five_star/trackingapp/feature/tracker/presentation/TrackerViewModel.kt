package dev.five_star.trackingapp.feature.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dev.five_star.trackingapp.core.location.controller.TrackingController
import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import dev.five_star.trackingapp.core.location.tracking.TrackingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TrackerViewModel(
    private val trackingStatus: TrackingStatus,
    private val trackingController: TrackingController
) : ViewModel() {

    private val zoom = MutableStateFlow(DEFAULT_ZOOM)

    val state = combine(
        trackingStatus.lastLocation,
        trackingStatus.uploadEnabled,
        zoom
    ) { location, uploadEnabled, zoom ->
        TrackerState(
            gpsStrength = location?.accuracy?.toGPSUiModel() ?: GpsStrength.NO_SIGNAL,
            isUploading = uploadEnabled,
            location = location?.toUiModel(),
            zoom = zoom
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackerState())

    fun onAction(action: TrackerAction) {
        when (action) {
            TrackerAction.OnUploadToggled -> trackingStatus.setUploadEnabled(!trackingStatus.uploadEnabled.value)
            is TrackerAction.UpdateZoom -> {
                // the map reports its initial zoom of 0 before the first location is known
                if (trackingStatus.lastLocation.value != null) {
                    zoom.value = action.zoom
                }
            }
            // the service ignores repeated starts, so calling this more than once is fine
            TrackerAction.OnPermissionGranted -> trackingController.start()
        }
    }

    private fun LocationModel.toUiModel(): LatLng = LatLng(latitude, longitude)

    private fun Float.toGPSUiModel(): GpsStrength {
        return when {
            this <= 5 -> GpsStrength.STRONG
            this <= 10 -> GpsStrength.GOOD
            this <= 20 -> GpsStrength.MEDIUM
            this <= 50 -> GpsStrength.WEAK
            else -> GpsStrength.NO_SIGNAL
        }
    }
}

class TrackerViewModelFactory(
    private val trackingStatus: TrackingStatus,
    private val trackingController: TrackingController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(trackingStatus, trackingController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
