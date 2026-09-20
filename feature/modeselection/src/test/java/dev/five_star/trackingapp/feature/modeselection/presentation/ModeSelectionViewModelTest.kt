package dev.five_star.trackingapp.feature.modeselection.presentation

import app.cash.turbine.test
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.core.settings.domain.GetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class ModeSelectionViewModelTest {

    private fun createViewModel(initialMode: AppMode = AppMode.UNDEFINED): Pair<ModeSelectionViewModel, FakeSettingsRepository> {
        val repository = FakeSettingsRepository(initialMode)
        val viewModel = ModeSelectionViewModel(
            setAppModeUseCase = SetAppModeUseCase(repository),
            getAppModeUseCase = GetAppModeUseCase(repository)
        )
        return viewModel to repository
    }

    @Test
    fun `onAction OnTrackerClicked updates state with Tracker destination and persists mode`() = runTest {
        val (viewModel, repository) = createViewModel()
        viewModel.state.test {
            //to not have initial state
            awaitItem()
            viewModel.onAction(ModeSelectionAction.OnTrackerClicked)
            assertEquals(AppMode.TRACKER, awaitItem().navigationTarget)
            assertEquals(AppMode.TRACKER, repository.currentMode())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAction OnObserver Clicked updates state with Observer destination and persists mode`() = runTest {
        val (viewModel, repository) = createViewModel()
        viewModel.state.test {
            //to not have initial state
            awaitItem()
            viewModel.onAction(ModeSelectionAction.OnObserverClicked)
            assertEquals(AppMode.OBSERVER, awaitItem().navigationTarget)
            assertEquals(AppMode.OBSERVER, repository.currentMode())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `previously saved mode is restored as navigation target on start`() = runTest {
        val (viewModel, _) = createViewModel(initialMode = AppMode.TRACKER)
        viewModel.state.test {
            //initial state before the saved mode is loaded
            assertEquals(AppMode.UNDEFINED, awaitItem().navigationTarget)
            assertEquals(AppMode.TRACKER, awaitItem().navigationTarget)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private class FakeSettingsRepository(initialMode: AppMode) : SettingsRepository {
        private val modeFlow = MutableStateFlow(initialMode)

        override suspend fun setAppMode(mode: AppMode) {
            modeFlow.value = mode
        }

        override fun getAppMode(): Flow<AppMode> = modeFlow.asStateFlow()

        fun currentMode(): AppMode = modeFlow.value
    }

}
