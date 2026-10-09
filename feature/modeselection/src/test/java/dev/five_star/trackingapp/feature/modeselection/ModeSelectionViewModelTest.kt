package dev.five_star.trackingapp.feature.modeselection

import dev.five_star.trackingapp.core.settings.data.SettingsRepository
import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ModeSelectionViewModelTest {

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @ParameterizedTest
    @EnumSource(value = AppMode::class, names = ["TRACKER", "OBSERVER"])
    fun `onAction OnModeSelected persists the selected mode`(mode: AppMode) {
        val repository = FakeSettingsRepository()
        val viewModel = ModeSelectionViewModel(repository)

        viewModel.onAction(ModeSelectionAction.OnModeSelected(mode))

        assertEquals(mode, repository.currentMode())
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val modeFlow = MutableStateFlow(AppMode.UNDEFINED)

        override suspend fun setAppMode(mode: AppMode) {
            modeFlow.value = mode
        }

        override fun getAppMode(): Flow<AppMode> = modeFlow.asStateFlow()

        override suspend fun setUploadEnabled(enabled: Boolean) = Unit

        override fun getUploadEnabled(): Flow<Boolean> = flowOf(false)

        fun currentMode(): AppMode = modeFlow.value
    }
}
