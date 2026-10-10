package dev.five_star.trackingapp.core.location.data

import dev.five_star.trackingapp.core.location.model.LocationModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryTrackingStatusTest {

    private val status = InMemoryTrackingStatus()
    private val location = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = 1_000L)

    @Test
    fun `initial state has no location and no signal`() {
        assertNull(status.lastLocation.value)
        assertFalse(status.isLocationAvailable.value)
    }

    @Test
    fun `updateLocation publishes latest location`() {
        status.updateLocation(location)

        assertEquals(location, status.lastLocation.value)
        assertTrue(status.isLocationAvailable.value)
    }

    @Test
    fun `losing availability keeps last location`() {
        status.updateLocation(location)

        status.setLocationAvailable(false)

        assertFalse(status.isLocationAvailable.value)
        assertEquals(location, status.lastLocation.value)
    }
}
