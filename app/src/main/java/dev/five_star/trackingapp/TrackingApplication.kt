package dev.five_star.trackingapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dev.five_star.trackingapp.core.location.LocationContainer
import dev.five_star.trackingapp.core.location.LocationContainerProvider
import dev.five_star.trackingapp.core.location.data.FirebaseLocationDataSource
import dev.five_star.trackingapp.core.settings.data.DataStoreSettingsRepository
import dev.five_star.trackingapp.core.settings.data.SettingsRepository

class TrackingApplication : Application(), LocationContainerProvider {

    // persistence keeps pending writes on disk, so locations recorded offline survive a process death.
    // It must be enabled before any other usage of the instance, which is why it is created only here.
    private val firebaseDatabase: FirebaseDatabase by lazy {
        Firebase.database(BuildConfig.FIREBASE_DATABASE_URL).apply {
            setPersistenceEnabled(true)
        }
    }

    override val locationContainer: LocationContainer by lazy {
        LocationContainer(
            context = this,
            remoteDataSource = FirebaseLocationDataSource(firebaseDatabase)
        )
    }

    val settingsRepository: SettingsRepository by lazy { DataStoreSettingsRepository(this) }
}
