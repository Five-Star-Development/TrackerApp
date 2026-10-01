package dev.five_star.trackingapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dev.five_star.trackingapp.core.location.data.FirebaseLocationRepository
import dev.five_star.trackingapp.core.location.tracking.LocationContainer
import dev.five_star.trackingapp.core.location.tracking.LocationContainerProvider
import dev.five_star.trackingapp.core.settings.data.SharedPreferencesSettingsRepository
import dev.five_star.trackingapp.core.settings.domain.GetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SettingsRepository

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
            repositories = listOf(FirebaseLocationRepository(firebaseDatabase))
        )
    }

    // single instance, otherwise every repository holds its own copy of the mode flow
    private val settingsRepository: SettingsRepository by lazy { SharedPreferencesSettingsRepository(this) }
    val setAppModeUseCase by lazy { SetAppModeUseCase(settingsRepository) }
    val getAppModeUseCase by lazy { GetAppModeUseCase(settingsRepository) }
}
