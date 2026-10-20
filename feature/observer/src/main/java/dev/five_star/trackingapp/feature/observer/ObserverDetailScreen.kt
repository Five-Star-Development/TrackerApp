package dev.five_star.trackingapp.feature.observer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.five_star.trackingapp.core.location.model.LocationModel

@Composable
fun ObserverDetailScreen(modifier: Modifier, id: String, viewModel: ObserverDetailViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserverDetailScreenContent(modifier, id, state)
}

@Composable
fun ObserverDetailScreenContent(modifier: Modifier, id: String, state: ObserverDetailState) {
    Box(
        modifier
            .fillMaxSize()
            .testTag("observerScreenContent"),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            ObserverDetailState.Loading -> CircularProgressIndicator()
            ObserverDetailState.Error -> Text(
                stringResource(R.string.observer_detail_error),
                color = MaterialTheme.colorScheme.error
            )
            is ObserverDetailState.Success -> Text("I am the observer of $id, ${state.locations.size} locations")
        }
    }
}



private class ObserverDetailStateProvider : PreviewParameterProvider<ObserverDetailState> {
    override val values = sequenceOf(
        ObserverDetailState.Loading,
        ObserverDetailState.Error,
        ObserverDetailState.Success(
            listOf(LocationModel(latitude = 52.52, longitude = 13.405, accuracy = 5f, time = 0L))
        )
    )
}

@PreviewLightDark
@Composable
private fun ObserverDetailScreenPreview(
    @PreviewParameter(ObserverDetailStateProvider::class) state: ObserverDetailState
) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface {
            ObserverDetailScreenContent(Modifier, "none", state)
        }
    }
}
