package dev.five_star.trackingapp.feature.modeselection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.five_star.trackingapp.core.settings.domain.SetAppModeUseCase
import kotlinx.coroutines.launch

class ModeSelectionViewModel(
    private val setAppModeUseCase: SetAppModeUseCase
) : ViewModel() {

    fun onAction(action: ModeSelectionAction) {
        when (action) {
            is ModeSelectionAction.OnModeSelected -> viewModelScope.launch {
                setAppModeUseCase(action.mode)
            }
        }
    }
}

class ModeSelectionViewModelFactory(
    private val setAppModeUseCase: SetAppModeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModeSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModeSelectionViewModel(setAppModeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
