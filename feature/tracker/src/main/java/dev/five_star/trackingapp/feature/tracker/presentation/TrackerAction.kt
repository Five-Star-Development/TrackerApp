package dev.five_star.trackingapp.feature.tracker.presentation

sealed interface TrackerAction {
    data object OnUploadToggled: TrackerAction
    data class UpdateZoom(val zoom: Float): TrackerAction
    data object OnPermissionGranted: TrackerAction
}
