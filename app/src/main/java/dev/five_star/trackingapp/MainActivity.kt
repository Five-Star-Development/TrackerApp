package dev.five_star.trackingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import dev.five_star.trackingapp.core.location.data.FirebaseLocationRepository
import dev.five_star.trackingapp.core.location.data.LocationDataSource
import dev.five_star.trackingapp.core.settings.data.SharedPreferencesSettingsRepository
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.core.settings.domain.GetAppModeUseCase
import dev.five_star.trackingapp.core.settings.domain.SetAppModeUseCase
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModel
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModelFactory
import dev.five_star.trackingapp.feature.modeselection.ui.ModeSelectionScreen
import dev.five_star.trackingapp.feature.observer.presentation.ObserverScreen
import dev.five_star.trackingapp.feature.tracker.presentation.TrackerScreen
import dev.five_star.trackingapp.feature.tracker.presentation.TrackerViewModel
import dev.five_star.trackingapp.feature.tracker.presentation.TrackerViewModelFactory
import dev.five_star.trackingapp.ui.theme.TrackingAppTheme


sealed class Destinations {
    data object ModeSelection : Destinations()
    data object Tracker : Destinations()
    data object Observer : Destinations()

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    val settingsRepository = remember { SharedPreferencesSettingsRepository(context) }
                    val setAppModeUseCase = remember { SetAppModeUseCase(settingsRepository) }
                    val getAppModeUseCase = remember { GetAppModeUseCase(settingsRepository) }

                    val backstack =
                        remember { mutableStateListOf<Destinations>(Destinations.ModeSelection) }

                    Log.d("MainActivity", "backstack: ${backstack.toList()}")

                    NavDisplay(
                        backStack = backstack,
                        onBack = { backstack.removeLastOrNull() },
                        // necessary because we want rememberViewModelStoreNavEntryDecorator
                        entryDecorators = listOf(
                            rememberSceneSetupNavEntryDecorator(),
                            rememberSavedStateNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<Destinations.ModeSelection> {
                                ModeSelectionScreen(
                                    viewModel = viewModel<ModeSelectionViewModel>(
                                        factory = ModeSelectionViewModelFactory(
                                            setAppModeUseCase,
                                            getAppModeUseCase
                                        )
                                    ),
                                    onNavigate = { mode ->
                                        val destination = when (mode) {
                                            AppMode.TRACKER -> Destinations.Tracker
                                            AppMode.OBSERVER -> Destinations.Observer
                                            AppMode.UNDEFINED -> null
                                        }
                                        destination?.let { backstack.add(it) }
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }

                            entry<Destinations.Tracker> {
                                val locationDataSource = remember { LocationDataSource(context.applicationContext) }
                                val locationRepository = remember {
                                    FirebaseLocationRepository(BuildConfig.FIREBASE_DATABASE_URL)
                                }
                                TrackerScreen(
                                    Modifier.padding(innerPadding),
                                    viewModel<TrackerViewModel>(factory = TrackerViewModelFactory(locationDataSource)),
                                    locationRepository
                                )
                            }

                            entry<Destinations.Observer> {
                                ObserverScreen(Modifier.padding(innerPadding))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackingAppTheme {
        Greeting("Android")
    }
}
