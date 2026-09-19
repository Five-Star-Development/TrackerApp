package dev.five_star.trackingapp.feature.modeselection.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModeSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun trackerClick_invokesOnNavigateWithTrackerMode() {
        var navigatedTo: AppMode? = null
        composeTestRule.setContent {
            ModeSelectionScreen(viewModel = ModeSelectionViewModel(), onNavigate = { navigatedTo = it })
        }

        composeTestRule.onNodeWithTag("TrackerButton").performClick()

        composeTestRule.waitForIdle()
        assertEquals(AppMode.TRACKER, navigatedTo)
    }

    @Test
    fun observerClick_invokesOnNavigateWithObserverMode() {
        var navigatedTo: AppMode? = null
        composeTestRule.setContent {
            ModeSelectionScreen(viewModel = ModeSelectionViewModel(), onNavigate = { navigatedTo = it })
        }

        composeTestRule.onNodeWithTag("ObserverButton").performClick()

        composeTestRule.waitForIdle()
        assertEquals(AppMode.OBSERVER, navigatedTo)
    }

}
