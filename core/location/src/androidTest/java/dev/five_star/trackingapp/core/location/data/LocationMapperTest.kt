package dev.five_star.trackingapp.core.location.data

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.location.model.LocationModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationMapperTest {

    @Test
    fun toModelMapsAllFields() {
        val location = Location("test").apply {
            latitude = 52.52
            longitude = 13.40
            accuracy = 7.5f
            time = 1_700_000_000_000L
        }

        assertEquals(
            LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 7.5f, time = 1_700_000_000_000L),
            location.toModel()
        )
    }
}
