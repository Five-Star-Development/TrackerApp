package dev.five_star.trackingapp.features.modeselection.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.five_star.trackingapp.Destinations


@Composable
fun ModeSelectionScreen(
    modifier: Modifier,
    viewModel: ModeSelectionViewModel,
    navigate: (Destinations) -> Unit
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    state.value.navigateTo?.let { destination ->
        navigate(destination)
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
            Modifier.weight(0.3f),
            "Tracker",
            "🛰"
        ) { viewModel.onAction(ModeSelectionAction.OnTrackerClicked) }

        ModeButton(
            Modifier.weight(0.3f),
            "Observer",
            "🗺"
        ) { viewModel.onAction(ModeSelectionAction.OnObserverClicked) }
    }
}

@Composable
fun ModeButton(modifier: Modifier = Modifier, name: String, icon: String, onClick: () -> Unit) {

    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (isPressed) 2.dp else 10.dp)

    Box(
        modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(elevation)
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease() // Warten bis losgelassen wird
                        } finally {
                            isPressed = false
                        }
                    })
            }
            .clickable(onClick = { onClick.invoke() })
    ) {
        Text(
            text = icon,
            fontSize = 96.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 16.dp)
        )
        Column(
            Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = " use this \n" + " device as", fontSize = 28.sp
            )
            Text(text = name, fontSize = 64.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun ModeSelectionPreview() {
    ModeSelectionScreen(Modifier, ModeSelectionViewModel(), {})
}
