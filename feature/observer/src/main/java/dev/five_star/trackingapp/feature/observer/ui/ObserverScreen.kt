package dev.five_star.trackingapp.feature.observer.presentation

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
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun ObserverScreen(modifier: Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .testTag("observerScreenContent"),
        contentAlignment = Alignment.Center
    ) {
        Text("Hello, I am the Observer")
    }
}

@PreviewLightDark
@Composable
private fun ObserverScreenPreview() {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface {
            ObserverScreen(Modifier)
        }
    }
}
