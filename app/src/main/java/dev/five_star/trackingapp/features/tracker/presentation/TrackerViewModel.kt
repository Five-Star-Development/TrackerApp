package dev.five_star.trackingapp.features.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TrackerViewModel : ViewModel() {

    private val _state = MutableStateFlow(TrackerState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TrackerState()
    )

    fun onAction(action: TrackerAction) {
        when (action) {
            is TrackerAction.OnTrackerClicked -> toggleTracking()
        }

    }

    private fun toggleTracking() {
        _state.update {
            it.copy(
                isTracking = !it.isTracking,
                // TODO: For testing only
                gpsStrength = GpsStrength.entries.toTypedArray().random()
            )
        }
    }

}