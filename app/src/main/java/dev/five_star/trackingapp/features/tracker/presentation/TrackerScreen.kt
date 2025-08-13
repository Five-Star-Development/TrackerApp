package dev.five_star.trackingapp.features.tracker.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun TrackerScreen(modifier: Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .testTag("trackerScreenContent"),
        contentAlignment = Alignment.Center
    ) {
        Text("Hello, I am the Tracker")
    }
}