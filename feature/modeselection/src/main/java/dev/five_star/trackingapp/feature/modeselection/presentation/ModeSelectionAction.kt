package dev.five_star.trackingapp.feature.modeselection.presentation

sealed class ModeSelectionAction {
    data object OnTrackerClicked : ModeSelectionAction()
    data object OnObserverClicked : ModeSelectionAction()
}
