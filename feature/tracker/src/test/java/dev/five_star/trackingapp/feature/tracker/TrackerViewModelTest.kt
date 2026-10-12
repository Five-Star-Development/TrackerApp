package dev.five_star.trackingapp.feature.tracker

import com.google.android.gms.maps.model.LatLng
import dev.five_star.trackingapp.core.location.data.TrackingStatus
import dev.five_star.trackingapp.core.location.model.LocationModel
import dev.five_star.trackingapp.core.location.service.TrackingController
import dev.five_star.trackingapp.core.settings.data.SettingsRepository
import dev.five_star.trackingapp.core.settings.model.AppMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModelTest {

    private val trackingStatus = FakeTrackingStatus()
    private val trackingController = FakeTrackingController()
    private val settingsRepository = FakeSettingsRepository()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // state uses WhileSubscribed, so it only updates while someone collects it
    private fun TestScope.createSubscribedViewModel(): TrackerViewModel {
        val viewModel = TrackerViewModel(trackingStatus, trackingController, settingsRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        return viewModel
    }

    @Test
    fun `without location the state shows no signal`() = runTest {
        val viewModel = createSubscribedViewModel()

        assertNull(viewModel.state.value.location)
        assertEquals(GpsStrength.NO_SIGNAL, viewModel.state.value.gpsStrength)
    }

    @Test
    fun `location from tracking status is shown`() = runTest {
        val viewModel = createSubscribedViewModel()

        trackingStatus.lastLocation.value = location(latitude = 52.52, longitude = 13.40)

        assertEquals(LatLng(52.52, 13.40), viewModel.state.value.location)
    }

    @ParameterizedTest
    @CsvSource("3, STRONG", "5, STRONG", "8, GOOD", "15, MEDIUM", "40, WEAK", "51, NO_SIGNAL")
    fun `accuracy is mapped to gps strength`(accuracy: Float, expected: GpsStrength) = runTest {
        val viewModel = createSubscribedViewModel()

        trackingStatus.lastLocation.value = location(accuracy = accuracy)
        trackingStatus.isLocationAvailable.value = true

        assertEquals(expected, viewModel.state.value.gpsStrength)
    }

    @Test
    fun `without available location the last location stays but shows no signal`() = runTest {
        val viewModel = createSubscribedViewModel()
        trackingStatus.lastLocation.value = location(latitude = 52.52, longitude = 13.40, accuracy = 3f)
        trackingStatus.isLocationAvailable.value = true
        assertEquals(GpsStrength.STRONG, viewModel.state.value.gpsStrength)

        // service stopped or signal lost
        trackingStatus.isLocationAvailable.value = false

        assertEquals(GpsStrength.NO_SIGNAL, viewModel.state.value.gpsStrength)
        assertEquals(LatLng(52.52, 13.40), viewModel.state.value.location)
    }

    @Test
    fun `OnUploadToggled toggles upload in settings`() = runTest {
        val viewModel = createSubscribedViewModel()

        viewModel.onAction(TrackerAction.OnUploadToggled)
        assertTrue(settingsRepository.uploadEnabled.value)
        assertTrue(viewModel.state.value.isUploading)

        viewModel.onAction(TrackerAction.OnUploadToggled)
        assertFalse(settingsRepository.uploadEnabled.value)
        assertFalse(viewModel.state.value.isUploading)
    }

    @Test
    fun `OnPermissionGranted starts tracking`() = runTest {
        val viewModel = createSubscribedViewModel()

        viewModel.onAction(TrackerAction.OnPermissionGranted)

        assertEquals(1, trackingController.startCount)
    }

    @Test
    fun `zoom updates are ignored until the first location is known`() = runTest {
        val viewModel = createSubscribedViewModel()

        viewModel.onAction(TrackerAction.UpdateZoom(0f))
        assertEquals(DEFAULT_ZOOM, viewModel.state.value.zoom)

        trackingStatus.lastLocation.value = location()
        viewModel.onAction(TrackerAction.UpdateZoom(10f))
        assertEquals(10f, viewModel.state.value.zoom)
    }

    private fun location(
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        accuracy: Float = 5f
    ) = LocationModel(latitude = latitude, longitude = longitude, accuracy = accuracy, time = 0L)

    private class FakeTrackingStatus : TrackingStatus {
        override val lastLocation = MutableStateFlow<LocationModel?>(null)
        override val isLocationAvailable = MutableStateFlow(false)
    }

    private class FakeSettingsRepository : SettingsRepository {
        val uploadEnabled = MutableStateFlow(false)

        override suspend fun setAppMode(mode: AppMode) = Unit

        override fun getAppMode(): Flow<AppMode> = flowOf(AppMode.TRACKER)

        override suspend fun setUploadEnabled(enabled: Boolean) {
            uploadEnabled.value = enabled
        }

        override fun getUploadEnabled(): Flow<Boolean> = uploadEnabled
    }

    private class FakeTrackingController : TrackingController {
        var startCount = 0

        override fun start() {
            startCount++
        }

        override fun stop() = Unit
    }
}
