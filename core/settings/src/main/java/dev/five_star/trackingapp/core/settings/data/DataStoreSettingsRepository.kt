package dev.five_star.trackingapp.core.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

internal const val LEGACY_PREFS_NAME = "app_settings_prefs"

/** Single DataStore instance per process; takes over the mode previously stored in SharedPreferences. */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, LEGACY_PREFS_NAME)) }
)

class DataStoreSettingsRepository internal constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    constructor(context: Context) : this(context.applicationContext.settingsDataStore)

    override suspend fun setAppMode(mode: AppMode) {
        dataStore.edit { it[KEY_APP_MODE] = mode.value }
    }

    override fun getAppMode(): Flow<AppMode> = dataStore.data
        // a corrupted or unreadable file falls back to defaults instead of crashing
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val value = prefs[KEY_APP_MODE] ?: AppMode.UNDEFINED.value
            AppMode.entries.find { it.value == value } ?: AppMode.UNDEFINED
        }

    internal companion object {
        // same name as the SharedPreferences key so SharedPreferencesMigration carries it over
        val KEY_APP_MODE = intPreferencesKey("app_mode")
    }
}
