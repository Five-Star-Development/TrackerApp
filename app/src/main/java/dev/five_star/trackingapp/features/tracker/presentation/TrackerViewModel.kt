package dev.five_star.trackingapp.features.tracker.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dev.five_star.trackingapp.features.tracker.data.LocationDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackerViewModel(private val locationDataSource: LocationDataSource) : ViewModel() {

    private val _state = MutableStateFlow(TrackerState())
    val state = _state.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), TrackerState()
        )

    fun onAction(action: TrackerAction) {
        when (action) {
            is TrackerAction.OnTrackerClicked -> toggleTracking()
            is TrackerAction.UpdateZoom -> updateZoom(action.zoom)
            TrackerAction.OnPermissionGranted -> {
//                observeGnssSignals()
                observeLocation()
            }
        }

    }

    private fun observeLocation() {
        viewModelScope.launch {
            locationDataSource.getLocationUpdates().collect { location ->
                _state.update {
                    val zoom = if (state.value.location == null) 15f else state.value.zoom
                    it.copy(
                        location = location.toUiModel(),
                        zoom = zoom,
                        gpsValue = location.accuracy.toDouble(),
                        gpsStrength = location.accuracy.toGPSUiModel()
                    )
                }
            }
        }
    }

    private fun toggleTracking() {
        _state.update {
            it.copy(
                isTracking = !it.isTracking
            )
        }
    }

    private fun updateZoom(newZoom: Float) {
        _state.update {
            it.copy(
                zoom = newZoom
            )
        }
    }

    private fun Location.toUiModel(): LatLng? = LatLng(latitude, longitude)

    private fun Double.toGPSUiModel(): GpsStrength {
        return when {
            this <= 0.0 -> GpsStrength.NO_SIGNAL
            this < 30 -> GpsStrength.WEAK
            this < 40 -> GpsStrength.MEDIUM
            this < 50 -> GpsStrength.GOOD
            this > 50 -> GpsStrength.STRONG
            else -> GpsStrength.NO_SIGNAL
        }
    }

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

class TrackerViewModelFactory(private val locationDataSource: LocationDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(locationDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}