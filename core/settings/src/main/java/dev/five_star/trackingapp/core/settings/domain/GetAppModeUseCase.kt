package dev.five_star.trackingapp.core.settings.domain

import kotlinx.coroutines.flow.Flow

class GetAppModeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppMode> = repository.getAppMode()
}
