package dev.five_star.trackingapp.core.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DataStoreSettingsRepositoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    // every test uses its own files, DataStore allows only one instance per file and process
    private val testName = "settings_test_${UUID.randomUUID()}"
    private val legacyPrefsName = "${testName}_legacy"
    private val legacyTrackingPrefsName = "${testName}_legacy_tracking"

    @After
    fun cleanUp() {
        context.preferencesDataStoreFile(testName).delete()
        context.deleteSharedPreferences(legacyPrefsName)
        context.deleteSharedPreferences(legacyTrackingPrefsName)
    }

    private fun TestScope.createDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            migrations = settingsMigrations(context, legacyPrefsName, legacyTrackingPrefsName),
            produceFile = { context.preferencesDataStoreFile(testName) }
        )

    @Test
    fun modeIsUndefinedWhenNothingWasSaved() = runTest {
        val repository = DataStoreSettingsRepository(createDataStore())

        assertEquals(AppMode.UNDEFINED, repository.getAppMode().first())
    }

    @Test
    fun setAppModeUpdatesFlow() = runTest {
        val repository = DataStoreSettingsRepository(createDataStore())

        repository.setAppMode(AppMode.TRACKER)

        assertEquals(AppMode.TRACKER, repository.getAppMode().first())
    }

    @Test
    fun modeSavedInSharedPreferencesIsMigrated() = runTest {
        context.getSharedPreferences(legacyPrefsName, Context.MODE_PRIVATE)
            .edit().putInt("app_mode", AppMode.OBSERVER.value).commit()

        val repository = DataStoreSettingsRepository(createDataStore())

        assertEquals(AppMode.OBSERVER, repository.getAppMode().first())
    }

    @Test
    fun uploadIsDisabledWhenNothingWasSaved() = runTest {
        val repository = DataStoreSettingsRepository(createDataStore())

        assertFalse(repository.getUploadEnabled().first())
    }

    @Test
    fun changedUploadSettingIsSeenByNextRead() = runTest {
        val repository = DataStoreSettingsRepository(createDataStore())
        assertFalse(repository.getUploadEnabled().first())

        // the service reads the setting per location while the UI may change it in between
        repository.setUploadEnabled(true)
        assertTrue(repository.getUploadEnabled().first())

        repository.setUploadEnabled(false)
        assertFalse(repository.getUploadEnabled().first())
    }

    @Test
    fun uploadSettingFromTrackingStatusPrefsIsMigrated() = runTest {
        context.getSharedPreferences(legacyTrackingPrefsName, Context.MODE_PRIVATE)
            .edit().putBoolean("upload_enabled", true).commit()

        val repository = DataStoreSettingsRepository(createDataStore())

        assertTrue(repository.getUploadEnabled().first())
    }
}
