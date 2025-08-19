package dev.five_star.trackingapp.features.tracker.presentation

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import dev.five_star.trackingapp.ui.theme.TrackingAppTheme

@Composable
fun TrackerScreen(modifier: Modifier, viewModel: TrackerViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val gpsStrength = state.value.gpsStrength
    val isTracking = state.value.isTracking
    val position = state.value.position

    TrackerScreenContent(modifier, gpsStrength, isTracking, position, viewModel::onAction)
}

@Composable
fun TrackerScreenContent(
    modifier: Modifier,
    gpsStrength: GpsStrength,
    isTracking: Boolean,
    position: LatLng?,
    onAction: (TrackerAction) -> Unit,
) {
    Column(
        modifier
            .testTag("trackerScreenContent")
            .fillMaxSize()
    ) {
        GPSStatus(Modifier.weight(0.2f), gpsStrength)
        Box(
            modifier
                .fillMaxWidth()
                .weight(0.20f), contentAlignment = Alignment.Center
        ) {
            ToggleTracking(isTracking) { onAction(TrackerAction.OnTrackerClicked) }
        }
        Text(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxWidth()
                .background(Color.Yellow),
            text = if (position != null) "Position: $position" else "Map here"
        )
    //TODO: get map key!
//        GoogleMap(modifier = Modifier
//            .weight(0.65f)
//            .fillMaxWidth()) {  }

    }
}

@Composable
fun GPSStatus(modifier: Modifier, strength: GpsStrength) {
    Box(
        modifier = modifier
            .testTag("GPSStatus")
            .fillMaxWidth()
            .background(strength.color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = strength.labelRes),
            modifier = Modifier.padding(16.dp),
            fontSize = 48.sp,
        )
    }
}

@Composable
fun ToggleTracking(isTracking: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(targetValue = if (isTracking) 90f else 0f)

    Box(
        modifier = Modifier
            .testTag("trackButton")
            .fillMaxWidth(0.25f)
            .aspectRatio(1f)
            .border(2.dp, LocalContentColor.current, CircleShape)
            .clip(CircleShape)
            .clickable { onToggle() }, contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isTracking) Icons.Filled.Close else Icons.Filled.PlayArrow,
            contentDescription = if (isTracking) "Stop" else "Track",
            modifier = Modifier
                .fillMaxSize(0.6f)
                .rotate(rotation)
        )
    }
}

@Preview(
    name = "Light Mode Preview", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true
)
@Preview(
    name = "Dark Mode Preview", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true
)
@Composable
fun TrackerContentPreview() {
    TrackingAppTheme {
        TrackerScreenContent(Modifier, GpsStrength.GOOD, true, LatLng(0.0, 0.0), {})
    }
}