package dev.five_star.trackingapp.feature.modeselection.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.five_star.trackingapp.core.settings.domain.AppMode
import dev.five_star.trackingapp.feature.modeselection.R
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionAction
import dev.five_star.trackingapp.feature.modeselection.presentation.ModeSelectionViewModel

@Composable
fun ModeSelectionScreen(
    viewModel: ModeSelectionViewModel,
    onNavigate: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    ModeSelectionContent(
        onModeSelected = { mode ->
            viewModel.onAction(ModeSelectionAction.OnModeSelected(mode))
            onNavigate(mode)
        },
        modifier = modifier
    )
}

@Composable
internal fun ModeSelectionContent(
    onModeSelected: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 32.dp, end = 16.dp)
    ) {

        Text(
            text = stringResource(R.string.mode_selection_title),
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
            stringResource(R.string.mode_tracker),
            "🛰"
        ) { onModeSelected(AppMode.TRACKER) }

        ModeButton(
            Modifier
                .weight(0.3f)
                .testTag("ObserverButton"),
            stringResource(R.string.mode_observer),
            "🗺"
        ) { onModeSelected(AppMode.OBSERVER) }
    }
}

@PreviewLightDark
@Composable
private fun ModeSelectionContentPreview() {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface {
            ModeSelectionContent(onModeSelected = {})
        }
    }
}
