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
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModel
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModelFactory
import dev.five_star.trackingapp.feature.modeselection.ui.ModeSelectionScreen
import dev.five_star.trackingapp.feature.observer.presentation.ObserverScreen
import dev.five_star.trackingapp.feature.tracker.presentation.TrackerScreen
import dev.five_star.trackingapp.feature.tracker.presentation.TrackerViewModel
import dev.five_star.trackingapp.feature.tracker.presentation.TrackerViewModelFactory
import dev.five_star.trackingapp.ui.theme.TrackingAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


sealed class Destinations {
    data object ModeSelection : Destinations()
    data object Tracker : Destinations()
    data object Observer : Destinations()

    companion object {
        private val all by lazy { listOf(ModeSelection, Tracker, Observer) }

        fun fromName(name: String): Destinations? = all.find { it.toString() == name }
    }
}

/** Keeps the back stack across rotation and process death, data objects are stored by name. */
private val BackStackSaver = listSaver(
    save = { backStack -> backStack.map { it.toString() } },
    restore = { names -> names.mapNotNull(Destinations::fromName).toMutableStateList() }
)

/** The saved mode is only evaluated on a fresh start; mode selection stays below so back leads to it. */
private fun initialBackStack(savedMode: AppMode): SnapshotStateList<Destinations> {
    val modeDestination = when (savedMode) {
        AppMode.TRACKER -> Destinations.Tracker
        AppMode.OBSERVER -> Destinations.Observer
        AppMode.UNDEFINED -> null
    }
    return listOfNotNull(Destinations.ModeSelection, modeDestination).toMutableStateList()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as TrackingApplication
        val locationContainer = app.locationContainer
        setContent {
            TrackingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val backstack = rememberSaveable(saver = BackStackSaver) {
                        // the settings repository is backed by SharedPreferences and a StateFlow,
                        // so first() returns immediately and does not block the main thread
                        initialBackStack(runBlocking { app.getAppModeUseCase().first() })
                    }

                    Log.d("MainActivity", "backstack: ${backstack.toList()}")

                    NavDisplay(
                        backStack = backstack,
                        onBack = {
                            // leaving the tracker via back stops the service; closing the app keeps it running
                            if (backstack.lastOrNull() == Destinations.Tracker) {
                                locationContainer.trackingController.stop()
                            }
                            backstack.removeLastOrNull()
                        },
                        // necessary because we want rememberViewModelStoreNavEntryDecorator
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<Destinations.ModeSelection> {
                                ModeSelectionScreen(
                                    viewModel = viewModel<ModeSelectionViewModel>(
                                        factory = ModeSelectionViewModelFactory(app.setAppModeUseCase)
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
                                TrackerScreen(
                                    Modifier.padding(innerPadding),
                                    viewModel<TrackerViewModel>(
                                        factory = TrackerViewModelFactory(
                                            locationContainer.trackingStatus,
                                            locationContainer.trackingController
                                        )
                                    )
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
