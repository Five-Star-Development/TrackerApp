package dev.five_star.trackingapp.core.settings.data

import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setAppMode(mode: AppMode)
    fun getAppMode(): Flow<AppMode>

    suspend fun setUploadEnabled(enabled: Boolean)

    /** Emits the current value on every collection, so readers always get the latest setting. */
    fun getUploadEnabled(): Flow<Boolean>
}
