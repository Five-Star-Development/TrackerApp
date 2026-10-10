package dev.five_star.trackingapp.feature.modeselection

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.settings.data.SettingsRepository
import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModeSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): ModeSelectionViewModel {
        val repository = FakeSettingsRepository()
        return ModeSelectionViewModel(settingsRepository = repository)
    }

    @Test
    fun trackerClick_invokesOnNavigateWithTrackerMode() {
        var navigatedTo: AppMode? = null
        composeTestRule.setContent {
            ModeSelectionScreen(viewModel = createViewModel(), onNavigate = { navigatedTo = it })
        }

        composeTestRule.onNodeWithTag("TrackerButton").performClick()

        composeTestRule.waitForIdle()
        assertEquals(AppMode.TRACKER, navigatedTo)
    }

    @Test
    fun observerClick_invokesOnNavigateWithObserverMode() {
        var navigatedTo: AppMode? = null
        composeTestRule.setContent {
            ModeSelectionScreen(viewModel = createViewModel(), onNavigate = { navigatedTo = it })
        }

        composeTestRule.onNodeWithTag("ObserverButton").performClick()

        composeTestRule.waitForIdle()
        assertEquals(AppMode.OBSERVER, navigatedTo)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val modeFlow = MutableStateFlow(AppMode.UNDEFINED)

        override suspend fun setAppMode(mode: AppMode) {
            modeFlow.value = mode
        }

        override fun getAppMode(): Flow<AppMode> = modeFlow.asStateFlow()

        override suspend fun setUploadEnabled(enabled: Boolean) = Unit

        override fun getUploadEnabled(): Flow<Boolean> = flowOf(false)
    }

}
