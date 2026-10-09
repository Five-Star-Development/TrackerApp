package dev.five_star.trackingapp.core.settings.data

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

// upload_enabled was stored by the tracking status in core:location before it became a setting
internal const val LEGACY_TRACKING_PREFS_NAME = "tracking_status_prefs"

/** Takes over the values previously stored in SharedPreferences. */
internal fun settingsMigrations(
    context: Context,
    legacyPrefsName: String = LEGACY_PREFS_NAME,
    legacyTrackingPrefsName: String = LEGACY_TRACKING_PREFS_NAME
): List<DataMigration<Preferences>> = listOf(
    SharedPreferencesMigration(context, legacyPrefsName),
    // the tracking prefs only ever held this key, so the file is deleted after the migration
    SharedPreferencesMigration(context, legacyTrackingPrefsName, setOf(DataStoreSettingsRepository.KEY_UPLOAD_ENABLED.name))
)

/** Single DataStore instance per process. */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings",
    produceMigrations = { context -> settingsMigrations(context) }
)

class DataStoreSettingsRepository internal constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    constructor(context: Context) : this(context.applicationContext.settingsDataStore)

    // a corrupted or unreadable file falls back to defaults instead of crashing
    private val preferences: Flow<Preferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    override suspend fun setAppMode(mode: AppMode) {
        dataStore.edit { it[KEY_APP_MODE] = mode.value }
    }

    override fun getAppMode(): Flow<AppMode> = preferences.map { prefs ->
        val value = prefs[KEY_APP_MODE] ?: AppMode.UNDEFINED.value
        AppMode.entries.find { it.value == value } ?: AppMode.UNDEFINED
    }

    override suspend fun setUploadEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_UPLOAD_ENABLED] = enabled }
    }

    override fun getUploadEnabled(): Flow<Boolean> = preferences.map { it[KEY_UPLOAD_ENABLED] ?: false }

    internal companion object {
        // same names as the SharedPreferences keys so SharedPreferencesMigration carries them over
        val KEY_APP_MODE = intPreferencesKey("app_mode")
        val KEY_UPLOAD_ENABLED = booleanPreferencesKey("upload_enabled")
    }
}
