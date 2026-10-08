package dev.five_star.trackingapp.core.settings.data

import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setAppMode(mode: AppMode)
    fun getAppMode(): Flow<AppMode>
}
