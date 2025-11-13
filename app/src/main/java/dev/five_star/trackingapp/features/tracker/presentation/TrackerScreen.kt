package dev.five_star.trackingapp.features.tracker.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.five_star.trackingapp.controller.LocationControllerManager
import dev.five_star.trackingapp.data.FirebaseLocationRepository
import dev.five_star.trackingapp.service.LocationService
import dev.five_star.trackingapp.ui.theme.TrackingAppTheme

@Composable
fun TrackerScreen(modifier: Modifier, viewModel: TrackerViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val gpsStrength = state.value.gpsStrength
    val isTracking = state.value.isTracking
    val location = state.value.location
    val zoom = state.value.zoom

    LocalContext.current.toggleTrackingService(isTracking)

    TrackerScreenContent(
        modifier = modifier,
        gpsStrength = gpsStrength,
        isTracking = isTracking,
        location = location,
        zoom = zoom,
        onTrackingToggled = { viewModel.onAction(TrackerAction.OnTrackerClicked) },
        onZoomChanged = { viewModel.onAction(TrackerAction.UpdateZoom(it)) },
        onPermissionGranted = { viewModel.onAction(TrackerAction.OnPermissionGranted) }
    )
}

@Composable
fun TrackerScreenContent(
    modifier: Modifier,
    gpsStrength: GpsStrength,
    isTracking: Boolean,
    location: LatLng?,
    zoom: Float,
    onTrackingToggled: () -> Unit = {},
    onZoomChanged: (Float) -> Unit = {},
    onPermissionGranted: () -> Unit = {},
) {
    Column(
        modifier
            .testTag("trackerScreenContent")
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocationPermissionHandler(onPermissionGranted = onPermissionGranted)
        GPSStatus(Modifier.weight(0.2f), gpsStrength)
//        Text("GPS Value: $gpsValue")
        ToggleTracking(Modifier.weight(0.3f), isTracking, onTrackingToggled)
        MapView(
            Modifier
                .weight(0.5f)
                .fillMaxWidth(), location, zoom, onZoomChanged
        )
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
fun ToggleTracking(modifier: Modifier = Modifier, isTracking: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(targetValue = if (isTracking) 90f else 0f)

    Box(
        modifier = modifier
            .testTag("trackButton")
            .fillMaxWidth(0.25f)
            .aspectRatio(1f)
            .border(2.dp, LocalContentColor.current, CircleShape)
            .clip(CircleShape)
            .clickable {
                val repository = FirebaseLocationRepository()
                LocationControllerManager.start(repository)
                onToggle()
            },
        contentAlignment = Alignment.Center
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

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapView(
    modifier: Modifier,
    location: LatLng?,
    zoom: Float,
    onZoomChanged: (Float) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 0f)
    }

    LaunchedEffect(location) {
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, zoom),
                1000
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        MapEffect(Unit) { map ->
            map.setOnCameraIdleListener {
                Log.d("MapView", "camera idle ${cameraPositionState.position}")
                onZoomChanged(cameraPositionState.position.zoom)
            }
        }

        location?.let {
            Marker(
                state = MarkerState(position = it)
            )
        }
    }
}

@Composable
fun LocationPermissionHandler(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) onPermissionGranted()
    }

    val findLocationState = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    Log.d("TrackerScreen", "findLocationState: $findLocationState")

    val permissionGranted = findLocationState == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            onPermissionGranted()
        } else {
            activity?.let {
                val test = ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                Log.d("TrackerScreen", "test: $test")
            }
            Log.d("TrackerScreen", "requesting permission")
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@PreviewLightDark
@Composable
fun TrackerContentPreview() {
    TrackingAppTheme {
        Scaffold { innerPadding ->
            TrackerScreenContent(
                modifier = Modifier.padding(innerPadding),
                GpsStrength.GOOD,
                true,
                LatLng(0.0, 0.0),
                0f,
            )
        }
    }
}


fun Context.toggleTrackingService(activate: Boolean) {
    Intent(this, LocationService::class.java).also {
        if (activate) {
            this.startService(it)
        } else {
            this.stopService(it)
        }
    }
}