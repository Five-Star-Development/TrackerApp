package dev.five_star.trackingapp.features.tracker.presentation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import dev.five_star.trackingapp.R

enum class GpsStrength(val color: Color, @param:StringRes val labelRes: Int) {
    NO_SIGNAL(Color(0xFFFF6961), R.string.gps_no_signal),
    WEAK(Color(0xFFFFD580), R.string.gps_weak),
    MEDIUM(Color(0xFFFFFFCD), R.string.gps_medium),
    GOOD(Color(0xFF77DD77), R.string.gps_good),
    STRONG(Color(0xFF89CFF0), R.string.gps_perfect)
}