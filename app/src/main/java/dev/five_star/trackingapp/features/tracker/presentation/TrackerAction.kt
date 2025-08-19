package dev.five_star.trackingapp.features.tracker.presentation

sealed interface TrackerAction {
    data object OnTrackerClicked: TrackerAction
}
