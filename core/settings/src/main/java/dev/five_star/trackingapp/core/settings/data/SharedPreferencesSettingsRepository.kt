package dev.five_star.trackingapp.core.settings.data

import android.content.Context
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.core.settings.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesSettingsRepository(context: Context) : SettingsRepository {

    private val sharedPreferences = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
    
    private val _modeFlow = MutableStateFlow(getCurrentMode())

    override suspend fun setAppMode(mode: AppMode) {
        sharedPreferences.edit().putInt("app_mode", mode.value).apply()
        _modeFlow.value = mode
    }

    override fun getAppMode(): Flow<AppMode> = _modeFlow.asStateFlow()

    private fun getCurrentMode(): AppMode {
        val value = sharedPreferences.getInt("app_mode", AppMode.UNDEFINED.value)
        return AppMode.entries.find { it.value == value } ?: AppMode.UNDEFINED
    }
}
