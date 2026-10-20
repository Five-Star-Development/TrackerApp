package dev.five_star.trackingapp.feature.observer

import dev.five_star.trackingapp.core.location.data.LocationHistoryRepository
import dev.five_star.trackingapp.core.location.model.LocationModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserverDetailViewModelTest {

    private val repository = FakeLocationHistoryRepository()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is loading until the locations arrive`() {
        val viewModel = ObserverDetailViewModel("tracker-a", repository)

        assertEquals(ObserverDetailState.Loading, viewModel.state.value)
    }

    @Test
    fun `state contains the loaded locations`() {
        val viewModel = ObserverDetailViewModel("tracker-a", repository)

        repository.result.complete(listOf(location(time = 1L)))

        assertEquals(ObserverDetailState.Success(listOf(location(time = 1L))), viewModel.state.value)
    }

    @Test
    fun `an empty history is a success, not an error`() {
        val viewModel = ObserverDetailViewModel("tracker-a", repository)

        repository.result.complete(emptyList())

        assertEquals(ObserverDetailState.Success(emptyList()), viewModel.state.value)
    }

    @Test
    fun `a failed load shows the error instead of crashing`() {
        val viewModel = ObserverDetailViewModel("tracker-a", repository)

        repository.result.completeExceptionally(IOException("no connection"))

        assertEquals(ObserverDetailState.Error, viewModel.state.value)
    }

    @Test
    fun `locations are loaded for the id of the entry`() {
        ObserverDetailViewModel("tracker-a", repository)

        assertEquals(listOf("tracker-a"), repository.requestedIds)
    }

    private fun location(time: Long) = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = time)

    /** Suspends in [load] until the test completes [result], so the loading state can be observed. */
    private class FakeLocationHistoryRepository : LocationHistoryRepository {
        val result = CompletableDeferred<List<LocationModel>>()
        val requestedIds = mutableListOf<String>()

        override suspend fun load(id: String): List<LocationModel> {
            requestedIds += id
            return result.await()
        }
    }
}
