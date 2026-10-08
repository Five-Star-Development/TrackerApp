package dev.five_star.trackingapp.feature.modeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.five_star.trackingapp.core.settings.data.SettingsRepository
import kotlinx.coroutines.launch

class ModeSelectionViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun onAction(action: ModeSelectionAction) {
        when (action) {
            is ModeSelectionAction.OnModeSelected -> viewModelScope.launch {
                settingsRepository.setAppMode(action.mode)
            }
        }
    }
}

class ModeSelectionViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModeSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModeSelectionViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
