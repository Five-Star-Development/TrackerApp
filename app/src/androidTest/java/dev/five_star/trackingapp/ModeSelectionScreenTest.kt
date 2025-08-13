package dev.five_star.trackingapp

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.features.modeselection.presentation.ModeSelectionScreen
import dev.five_star.trackingapp.features.modeselection.presentation.ModeSelectionViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModeSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun trackerClick_navigatesToTrackerScreen() {
        composeTestRule.setContent {
            ModeSelectionScreen(Modifier.padding(8.dp), ModeSelectionViewModel()) { }
        }

        composeTestRule.onNodeWithTag("TrackerButton").performClick()

        composeTestRule.onNodeWithTag("trackerScreenContent").isDisplayed()
    }

    @Test
    fun observerClick_navigatesToObserverScreen() {
        composeTestRule.setContent {
            ModeSelectionScreen(Modifier.padding(8.dp), ModeSelectionViewModel()) { }
        }

        composeTestRule.onNodeWithTag("ObserverButton").performClick()

        composeTestRule.onNodeWithTag("observerScreenContent").isDisplayed()
    }

}