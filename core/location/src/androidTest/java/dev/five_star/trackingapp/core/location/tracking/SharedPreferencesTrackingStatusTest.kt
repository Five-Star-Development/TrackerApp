package dev.five_star.trackingapp.core.location.tracking

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.five_star.trackingapp.core.location.domain.model.LocationModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesTrackingStatusTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    @After
    fun clearPreferences() {
        context.getSharedPreferences("tracking_status_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun initialStateHasNoLocationAndUploadDisabled() {
        val status = SharedPreferencesTrackingStatus(context)

        assertNull(status.lastLocation.value)
        assertFalse(status.uploadEnabled.value)
    }

    @Test
    fun uploadEnabledIsRestoredByNewInstance() {
        SharedPreferencesTrackingStatus(context).setUploadEnabled(true)

        val restored = SharedPreferencesTrackingStatus(context)

        assertTrue(restored.uploadEnabled.value)
    }

    @Test
    fun updateLocationPublishesLatestLocation() {
        val status = SharedPreferencesTrackingStatus(context)
        val location = LocationModel(latitude = 52.52, longitude = 13.40, accuracy = 4f, time = 1_000L)

        status.updateLocation(location)

        assertEquals(location, status.lastLocation.value)
    }

    @Test
    fun lastLocationIsNotPersisted() {
        SharedPreferencesTrackingStatus(context)
            .updateLocation(LocationModel(latitude = 1.0, longitude = 2.0, accuracy = 3f, time = 4L))

        assertNull(SharedPreferencesTrackingStatus(context).lastLocation.value)
    }
}
