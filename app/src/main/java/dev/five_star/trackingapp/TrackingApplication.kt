package dev.five_star.trackingapp

import android.app.Application
import dev.five_star.trackingapp.core.location.data.FirebaseLocationRepository
import dev.five_star.trackingapp.core.location.tracking.LocationContainer
import dev.five_star.trackingapp.core.location.tracking.LocationContainerProvider
import dev.five_star.trackingapp.core.settings.data.SharedPreferencesSettingsRepository
import dev.five_star.trackingapp.core.settings.domain.GetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SettingsRepository

class TrackingApplication : Application(), LocationContainerProvider {

    override val locationContainer: LocationContainer by lazy {
        LocationContainer(
            context = this,
            repositories = listOf(FirebaseLocationRepository(BuildConfig.FIREBASE_DATABASE_URL))
        )
    }

    // single instance, otherwise every repository holds its own copy of the mode flow
    private val settingsRepository: SettingsRepository by lazy { SharedPreferencesSettingsRepository(this) }
    val setAppModeUseCase by lazy { SetAppModeUseCase(settingsRepository) }
    val getAppModeUseCase by lazy { GetAppModeUseCase(settingsRepository) }
}
