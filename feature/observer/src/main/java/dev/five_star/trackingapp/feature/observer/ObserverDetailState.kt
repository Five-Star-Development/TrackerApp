package dev.five_star.trackingapp.feature.observer

import dev.five_star.trackingapp.core.location.model.LocationModel

sealed interface ObserverDetailState {
    data object Loading : ObserverDetailState
    data class Success(val locations: List<LocationModel>) : ObserverDetailState
    data object Error : ObserverDetailState
}
