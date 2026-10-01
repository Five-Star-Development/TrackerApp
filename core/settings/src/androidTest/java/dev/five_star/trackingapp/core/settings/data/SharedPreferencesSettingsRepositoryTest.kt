package dev.five_star.trackingapp.core.settings.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.settings.domain.AppMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesSettingsRepositoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    @After
    fun clearPreferences() {
        context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun modeIsUndefinedWhenNothingWasSaved() = runTest {
        val repository = SharedPreferencesSettingsRepository(context)

        assertEquals(AppMode.UNDEFINED, repository.getAppMode().first())
    }

    @Test
    fun setAppModeUpdatesFlow() = runTest {
        val repository = SharedPreferencesSettingsRepository(context)

        repository.setAppMode(AppMode.TRACKER)

        assertEquals(AppMode.TRACKER, repository.getAppMode().first())
    }

    @Test
    fun savedModeIsRestoredByNewInstance() = runTest {
        SharedPreferencesSettingsRepository(context).setAppMode(AppMode.OBSERVER)

        val restored = SharedPreferencesSettingsRepository(context)

        assertEquals(AppMode.OBSERVER, restored.getAppMode().first())
    }
}
