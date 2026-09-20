package dev.five_star.trackingapp.feature.modeselection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.core.settings.domain.GetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SetAppModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ModeSelectionViewModel(
    private val setAppModeUseCase: SetAppModeUseCase,
    private val getAppModeUseCase: GetAppModeUseCase
) : ViewModel() {

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
        viewModelScope.launch {
            getAppModeUseCase().collect { savedMode ->
                if (savedMode != AppMode.UNDEFINED) {
                    _state.update { it.copy(navigationTarget = savedMode) }
                }
            }
        }
    }

    private fun navigateToTracker() {
        viewModelScope.launch {
            setAppModeUseCase(AppMode.TRACKER)
            _state.update { it.copy(navigationTarget = AppMode.TRACKER) }
        }
    }

    private fun navigateToObserver() {
        viewModelScope.launch {
            setAppModeUseCase(AppMode.OBSERVER)
            _state.update { it.copy(navigationTarget = AppMode.OBSERVER) }
        }
    }

    fun onNavigationConsumed() {
        _state.update { it.copy(navigationTarget = AppMode.UNDEFINED) }
    }
}

class ModeSelectionViewModelFactory(
    private val setAppModeUseCase: SetAppModeUseCase,
    private val getAppModeUseCase: GetAppModeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModeSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModeSelectionViewModel(setAppModeUseCase, getAppModeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
