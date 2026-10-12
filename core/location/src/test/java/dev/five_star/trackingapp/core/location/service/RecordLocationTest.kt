package dev.five_star.trackingapp.core.location.service

import dev.five_star.trackingapp.core.location.data.InMemoryTrackingStatus
import dev.five_star.trackingapp.core.location.data.LocationRemoteDataSource
import dev.five_star.trackingapp.core.location.data.LocationRepository
import dev.five_star.trackingapp.core.location.model.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecordLocationTest {

    private val trackingStatus = InMemoryTrackingStatus()
    private val uploadEnabled = MutableStateFlow(false)
    private val remoteDataSource = FakeLocationRemoteDataSource()
    private val locationRepository = LocationRepository(remoteDataSource)

    private suspend fun record(location: LocationModel) =
        recordLocation(location, trackingStatus, uploadEnabled, locationRepository)

    @Test
    fun `location is always published to the tracking status`() = runTest {
        record(location(time = 1L))

        assertEquals(location(time = 1L), trackingStatus.lastLocation.value)
    }

    @Test
    fun `location is not uploaded while upload is disabled`() = runTest {
        uploadEnabled.value = false

        record(location(time = 1L))

        assertTrue(remoteDataSource.saved.isEmpty())
    }

    @Test
    fun `location is uploaded while upload is enabled`() = runTest {
        uploadEnabled.value = true

        record(location(time = 1L))

        assertEquals(listOf(location(time = 1L)), remoteDataSource.saved)
    }

    @Test
    fun `switching upload while recording affects the following locations`() = runTest {
        record(location(time = 1L))
        uploadEnabled.value = true
        record(location(time = 2L))
        uploadEnabled.value = false
        record(location(time = 3L))

        assertEquals(listOf(location(time = 2L)), remoteDataSource.saved)
        assertEquals(location(time = 3L), trackingStatus.lastLocation.value)
    }

    private fun location(time: Long) = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = time)

    private class FakeLocationRemoteDataSource : LocationRemoteDataSource {
        val saved = mutableListOf<LocationModel>()

        override suspend fun save(location: LocationModel) {
            saved += location
        }
    }
}
