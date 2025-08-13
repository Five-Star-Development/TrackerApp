package dev.five_star.trackingapp

import app.cash.turbine.test
import dev.five_star.trackingapp.features.modeselection.presentation.ModeSelectionAction
import dev.five_star.trackingapp.features.modeselection.presentation.ModeSelectionViewModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class ModeSelectionViewModelTest {

    private lateinit var viewModel: ModeSelectionViewModel

    @Test
    fun `onAction OnTrackerClicked updates state with Tracker destination`() = runTest {
        viewModel = ModeSelectionViewModel()
        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ModeSelectionAction.OnTrackerClicked)
            assertEquals(Destinations.Tracker, awaitItem().navigateTo)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAction OnObserver Clicked updates state with Observer destination`() = runTest {
        viewModel = ModeSelectionViewModel()
        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ModeSelectionAction.OnObserverClicked)
            assertEquals(Destinations.Observer, awaitItem().navigateTo)

            cancelAndIgnoreRemainingEvents()
        }
    }

}