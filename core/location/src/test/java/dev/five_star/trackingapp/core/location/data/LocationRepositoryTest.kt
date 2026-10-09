package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationRepositoryTest {

    private val trackingStatus = FakeTrackingStatus()
    private val remoteDataSource = FakeLocationRemoteDataSource()
    private val repository = LocationRepository(trackingStatus, remoteDataSource)

    private val location = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = 1_000L)

    @Test
    fun `location is not uploaded while upload is disabled`() = runTest {
        trackingStatus.setUploadEnabled(false)

        repository.upload(location)

        assertTrue(remoteDataSource.saved.isEmpty())
    }

    @Test
    fun `location is uploaded while upload is enabled`() = runTest {
        trackingStatus.setUploadEnabled(true)

        repository.upload(location)

        assertEquals(listOf(location), remoteDataSource.saved)
    }

    private class FakeTrackingStatus : TrackingStatus {
        override val lastLocation = MutableStateFlow<LocationModel?>(null)
        override val isLocationAvailable = MutableStateFlow(false)
        override val uploadEnabled = MutableStateFlow(false)

        override fun setUploadEnabled(enabled: Boolean) {
            uploadEnabled.value = enabled
        }
    }

    private class FakeLocationRemoteDataSource : LocationRemoteDataSource {
        val saved = mutableListOf<LocationModel>()

        override suspend fun save(location: LocationModel) {
            saved += location
        }
    }
}
