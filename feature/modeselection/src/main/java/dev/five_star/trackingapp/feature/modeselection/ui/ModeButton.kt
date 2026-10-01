package dev.five_star.trackingapp.feature.modeselection.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModeButton(modifier: Modifier = Modifier, name: String, icon: String, onClick: () -> Unit) {

    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (isPressed) 2.dp else 10.dp)

    Box(
        modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(elevation)
            .background(CardDefaults.cardColors().containerColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    }
                )
            }
            .clickable(onClick = { onClick() })
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
