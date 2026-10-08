package dev.five_star.trackingapp.feature.modeselection

import dev.five_star.trackingapp.core.settings.model.AppMode

sealed class ModeSelectionAction {
    data class OnModeSelected(val mode: AppMode) : ModeSelectionAction()
}
