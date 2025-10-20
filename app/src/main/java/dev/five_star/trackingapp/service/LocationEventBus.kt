package dev.five_star.trackingapp.service

import android.location.Location
import kotlinx.coroutines.flow.MutableSharedFlow

object LocationEventBus {
    val location = MutableSharedFlow<Location>(
        replay = 0,
        extraBufferCapacity = 1
    )
}