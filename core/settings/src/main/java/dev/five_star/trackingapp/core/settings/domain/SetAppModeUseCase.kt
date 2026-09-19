package dev.five_star.trackingapp.core.settings.domain

class SetAppModeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(mode: AppMode) {
        repository.setAppMode(mode)
    }
}
