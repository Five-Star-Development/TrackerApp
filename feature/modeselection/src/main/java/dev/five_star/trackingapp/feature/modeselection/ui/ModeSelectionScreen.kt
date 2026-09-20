package dev.five_star.trackingapp.feature.modeselection.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionAction
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionState
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModel

@Composable
fun ModeSelectionScreen(
    viewModel: ModeSelectionViewModel,
    onNavigate: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ModeSelectionContent(
        state = state,
        onAction = { viewModel.onAction(it) },
        onNavigate = onNavigate,
        onNavigationConsumed = { viewModel.onNavigationConsumed() },
        modifier = modifier
    )
}

@Composable
internal fun ModeSelectionContent(
    state: ModeSelectionState,
    onAction: (ModeSelectionAction) -> Unit,
    onNavigate: (AppMode) -> Unit,
    onNavigationConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(state.navigationTarget) {
        if (state.navigationTarget != AppMode.UNDEFINED) {
            onNavigate(state.navigationTarget)
            onNavigationConsumed()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 32.dp, end = 16.dp)
    ) {

        Text(
            text = "Select if you want to use this device as a Tracker or as a Observer",
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .weight(0.4f),
            style = TextStyle(
                fontSize = 36.sp, lineHeight = 40.sp
            ),
        )

        ModeButton(
            Modifier
                .weight(0.3f)
                .testTag("TrackerButton"),
            "Tracker",
            "🛰"
        ) { onAction(ModeSelectionAction.OnTrackerClicked) }

        ModeButton(
            Modifier
                .weight(0.3f)
                .testTag("ObserverButton"),
            "Observer",
            "🗺"
        ) { onAction(ModeSelectionAction.OnObserverClicked) }
    }
}
