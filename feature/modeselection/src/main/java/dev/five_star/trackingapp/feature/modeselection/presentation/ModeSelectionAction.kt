package dev.five_star.trackingapp.feature.modeselection.presentation

import dev.five_star.trackingapp.core.settings.domain.AppMode

sealed class ModeSelectionAction {
    data class OnModeSelected(val mode: AppMode) : ModeSelectionAction()
}
