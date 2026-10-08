package dev.five_star.trackingapp.core.location.domain.usecase

import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import dev.five_star.trackingapp.core.location.domain.repository.LocationRepository
import dev.five_star.trackingapp.core.location.tracking.MutableTrackingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecordLocationUseCaseTest {

    private val trackingStatus = FakeMutableTrackingStatus()
    private val firstRepository = FakeLocationRepository()
    private val secondRepository = FakeLocationRepository()
    private val recordLocation = RecordLocationUseCase(trackingStatus, listOf(firstRepository, secondRepository))

    private val location = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = 1_000L)

    @Test
    fun `location is always published to the tracking status`() = runTest {
        recordLocation(location)

        assertEquals(location, trackingStatus.lastLocation.value)
    }

    @Test
    fun `location is not saved while upload is disabled`() = runTest {
        trackingStatus.setUploadEnabled(false)

        recordLocation(location)

        assertTrue(firstRepository.saved.isEmpty())
        assertTrue(secondRepository.saved.isEmpty())
    }

    @Test
    fun `location is saved to all repositories while upload is enabled`() = runTest {
        trackingStatus.setUploadEnabled(true)

        recordLocation(location)

        assertEquals(listOf(location), firstRepository.saved)
        assertEquals(listOf(location), secondRepository.saved)
    }

    private class FakeMutableTrackingStatus : MutableTrackingStatus {
        override val lastLocation = MutableStateFlow<LocationModel?>(null)
        override val isLocationAvailable = MutableStateFlow(false)
        override val uploadEnabled = MutableStateFlow(false)

        override fun setUploadEnabled(enabled: Boolean) {
            uploadEnabled.value = enabled
        }

        override fun updateLocation(location: LocationModel) {
            lastLocation.value = location
        }

        override fun setLocationAvailable(available: Boolean) {
            isLocationAvailable.value = available
        }
    }

    private class FakeLocationRepository : LocationRepository {
        val saved = mutableListOf<LocationModel>()

        override suspend fun save(location: LocationModel) {
            saved += location
        }
    }
}
