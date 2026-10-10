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

/**
 * Implemented by the Application so components created by Android, like services,
 * can reach the [SettingsRepository] without depending on the app module.
 */
interface SettingsRepositoryProvider {
    val settingsRepository: SettingsRepository
}
