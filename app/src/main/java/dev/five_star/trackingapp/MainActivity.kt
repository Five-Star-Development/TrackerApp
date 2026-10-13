package dev.five_star.trackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.five_star.trackingapp.core.settings.model.AppMode
import dev.five_star.trackingapp.feature.modeselection.ModeSelectionScreen
import dev.five_star.trackingapp.feature.modeselection.ModeSelectionViewModel
import dev.five_star.trackingapp.feature.modeselection.ModeSelectionViewModelFactory
import dev.five_star.trackingapp.feature.observer.ObserverDetailScreen
import dev.five_star.trackingapp.feature.observer.ObserverScreen
import dev.five_star.trackingapp.feature.tracker.TrackerScreen
import dev.five_star.trackingapp.feature.tracker.TrackerViewModel
import dev.five_star.trackingapp.feature.tracker.TrackerViewModelFactory
import dev.five_star.trackingapp.ui.theme.TrackingAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


sealed class Destinations {
    data object ModeSelection : Destinations()
    data object Tracker : Destinations()
    data object ObserverList : Destinations()
    data class ObserverDetail(val id: String) : Destinations()

    companion object {
        private val all by lazy { listOf(ModeSelection, Tracker, ObserverList) }

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
        AppMode.OBSERVER_LIST -> Destinations.ObserverList
        AppMode.UNDEFINED -> null
    }
    return listOfNotNull(Destinations.ModeSelection, modeDestination).toMutableStateList()
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as TrackingApplication
        val locationContainer = app.locationContainer

        // null while the saved mode is loading from DataStore, the splash screen stays visible meanwhile.
        // After recreation the back stack is restored from saved state, so nothing has to be loaded.
        var startMode by mutableStateOf(if (savedInstanceState != null) AppMode.UNDEFINED else null)
        if (startMode == null) {
            lifecycleScope.launch { startMode = app.settingsRepository.getAppMode().first() }
        }
        splashScreen.setKeepOnScreenCondition { startMode == null }

        setContent {
            val mode = startMode ?: return@setContent
            TrackingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val backstack = rememberSaveable(saver = BackStackSaver) { initialBackStack(mode) }
                    val listDetailStrategy = rememberListDetailSceneStrategy<Destinations>()
                    val sceneStrategy = remember(listDetailStrategy) { listOf(listDetailStrategy)}

                    NavDisplay(
                        backStack = backstack,
                        sceneStrategies = sceneStrategy,
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
                                        factory = ModeSelectionViewModelFactory(app.settingsRepository)
                                    ),
                                    onNavigate = { mode ->
                                        val destination = when (mode) {
                                            AppMode.TRACKER -> Destinations.Tracker
                                            AppMode.OBSERVER_LIST -> Destinations.ObserverList
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
                                            locationContainer.trackingController,
                                            app.settingsRepository
                                        )
                                    )
                                )
                            }

                            entry<Destinations.ObserverList>(
                                metadata = ListDetailSceneStrategy.listPane()
                            ) {
                                ObserverScreen(
                                    Modifier.padding(innerPadding),
                                    onItemSelected = { id -> backstack.add(Destinations.ObserverDetail(id)) }
                                )
                            }

                            entry<Destinations.ObserverDetail>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                key -> ObserverDetailScreen(Modifier.padding(innerPadding), key.id)
                            }
                        }
                    )
                }
            }
        }
    }
}
