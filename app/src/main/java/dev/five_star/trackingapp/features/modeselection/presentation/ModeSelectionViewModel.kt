package dev.five_star.trackingapp.features.modeselection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.five_star.trackingapp.Destinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ModeSelectionViewModel : ViewModel() {

    private val _state = MutableStateFlow(ModeSelectionState())
    val state = _state
        .onStart { checkSelection() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ModeSelectionState())

    fun onAction(action: ModeSelectionAction) {
        when (action) {
            ModeSelectionAction.OnTrackerClicked -> navigateToTracker()
            ModeSelectionAction.OnObserverClicked -> navigateToObserver()
        }
    }

    private fun checkSelection() {
        //TODO
    }

    private fun navigateToTracker() {
        _state.value = _state.value.copy(navigateTo = Destinations.Tracker)
    }

    private fun navigateToObserver() {
        _state.value = _state.value.copy(navigateTo = Destinations.Observer)
    }

}