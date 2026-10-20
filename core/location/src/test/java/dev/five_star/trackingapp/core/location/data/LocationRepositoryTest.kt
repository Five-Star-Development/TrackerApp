package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LocationRepositoryTest {

    private val remoteDataSource = FakeLocationRemoteDataSource()
    private val repository = LocationRepository(remoteDataSource)

    @Test
    fun `upload saves the location in the remote data source`() = runTest {
        repository.upload(location(time = 1L))

        assertEquals(listOf(location(time = 1L)), remoteDataSource.saved)
    }

    @Test
    fun `load returns the locations of the requested id`() = runTest {
        remoteDataSource.stored["tracker-a"] = listOf(location(time = 1L), location(time = 2L))
        remoteDataSource.stored["tracker-b"] = listOf(location(time = 3L))

        assertEquals(listOf(location(time = 1L), location(time = 2L)), repository.load("tracker-a"))
    }

    @Test
    fun `load returns an empty list for an unknown id`() = runTest {
        assertEquals(emptyList(), repository.load("unknown"))
    }

    private fun location(time: Long) = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = time)

    private class FakeLocationRemoteDataSource : LocationRemoteDataSource {
        val saved = mutableListOf<LocationModel>()
        val stored = mutableMapOf<String, List<LocationModel>>()

        override suspend fun save(location: LocationModel) {
            saved += location
        }

        override suspend fun load(id: String): List<LocationModel> = stored[id].orEmpty()
    }
}
