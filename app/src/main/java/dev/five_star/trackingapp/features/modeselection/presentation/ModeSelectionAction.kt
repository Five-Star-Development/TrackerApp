package dev.five_star.trackingapp.features.modeselection.presentation

sealed interface ModeSelectionAction {
    object OnTrackerClicked: ModeSelectionAction
    object OnObserverClicked: ModeSelectionAction
}