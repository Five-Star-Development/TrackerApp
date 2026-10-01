package dev.five_star.trackingapp.core.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setAppMode(mode: AppMode)
    fun getAppMode(): Flow<AppMode>
}
