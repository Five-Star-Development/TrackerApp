package dev.five_star.trackingapp.core.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.settings.domain.AppMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DataStoreSettingsRepositoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    // every test uses its own files, DataStore allows only one instance per file and process
    private val testName = "settings_test_${UUID.randomUUID()}"
    private val legacyPrefsName = "${testName}_legacy"

    @After
    fun cleanUp() {
        context.preferencesDataStoreFile(testName).delete()
        context.deleteSharedPreferences(legacyPrefsName)
    }

    private fun TestScope.createDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            migrations = listOf(SharedPreferencesMigration(context, legacyPrefsName)),
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
}
