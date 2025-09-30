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
import dev.five_star.trackingapp.features.modeselection.presentation.ModeSelectionScreen
import dev.five_star.trackingapp.features.modeselection.presentation.ModeSelectionViewModel
import dev.five_star.trackingapp.features.observer.presentation.ObserverScreen
import dev.five_star.trackingapp.features.tracker.data.LocationDataSource
import dev.five_star.trackingapp.features.tracker.presentation.TrackerScreen
import dev.five_star.trackingapp.features.tracker.presentation.TrackerViewModel
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
                                    Modifier.padding(innerPadding),
                                    viewModel<ModeSelectionViewModel>()
                                ) { direction ->
                                    backstack.add(direction)
                                }
                            }

                            entry<Destinations.Tracker> {
                                val context = LocalContext.current
                                val locationDataSource = LocationDataSource(context)
                                TrackerScreen(Modifier.padding(innerPadding), TrackerViewModel(locationDataSource))
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