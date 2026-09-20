package dev.five_star.trackingapp.feature.modeselection.presentation

import dev.five_star.trackingapp.core.settings.domain.AppMode

data class ModeSelectionState(
    val navigationTarget: AppMode = AppMode.UNDEFINED,
)
