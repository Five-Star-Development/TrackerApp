package dev.five_star.trackingapp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModeSelectionScreen(modifier: Modifier) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Select if you want to use this device as a Tracker or as a Observer",
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxHeight(0.35f),
            style = TextStyle(
                fontSize = 36.sp, lineHeight = 36.sp
            ),
        )

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.60f)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(2.dp, color = Color.Gray)
            ) {
                Text(
                    text = "🛰",
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
                        text = " use this \n" + " device as a", fontSize = 26.sp
                    )
                    Text(text = "Tracker", fontSize = 64.sp)
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(2.dp, color = Color.Gray)
            ) {
                Text(
                    text = "🗺",
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
                        text = " use this \n" + " device as a", fontSize = 26.sp
                    )
                    Text(text = "Observer", fontSize = 64.sp)
                }
            }
        }
    }
}
