package dev.five_star.trackingapp.feature.observer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun ObserverListScreen(modifier: Modifier, onItemSelected: (id: String) -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .testTag("observerScreenContent"),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.observer_placeholder),
            Modifier.clickable { onItemSelected("AddFirebaseAuthUID") })
    }
}

@PreviewLightDark
@Composable
private fun ObserverListScreenPreview() {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface {
            ObserverListScreen(Modifier, {})
        }
    }
}
