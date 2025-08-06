package dev.five_star.trackingapp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModeSelectionScreen(modifier: Modifier) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxHeight(0.39f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select if you want to use this device as a Tracker or as a Observer",
                style = TextStyle(
                    fontSize = 36.sp, lineHeight = 40.sp
                ),
            )
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.60f)
                .fillMaxWidth()
        ) {

            ModeButton(Modifier.weight(1f), "Tracker", "🛰")
            ModeButton(Modifier.weight(1f), "Observer", "🗺")
        }
    }
}

@Composable
fun ModeButton(modifier: Modifier = Modifier, name: String, icon: String) {

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
                    }
                )
            }
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
            Text(text = name, fontSize = 64.sp)
        }
    }
}
