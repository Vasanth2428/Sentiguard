package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

data class NailCheckState(
    val instructions: String = "Place your fingernail under the camera",
    val isResultAvailable: Boolean = false,
    val resultMessage: String = ""
)

sealed class NailCheckEvent {
    data object CaptureImage : NailCheckEvent()
    data object Reset : NailCheckEvent()
}

@Composable
fun NailCheckScreen(
    state: NailCheckState,
    onEvent: (NailCheckEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0) // Handle manually
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
             Text(
                text = "Nail Color Analysis",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Camera Preview Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.5f), RoundedCornerShape(24.dp))
                    .background(Color.Black), // Camera preview is always black-ish
                contentAlignment = Alignment.Center
            ) {
                // Viewfinder Frame (Red Corners)
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )
                
                // Instructions / Overlay
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha=0.6f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Position fingernails within frame",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = { if (!state.isResultAvailable) onEvent(NailCheckEvent.CaptureImage) else onEvent(NailCheckEvent.Reset) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ThumbUp, null) // Camera icon ideally
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isResultAvailable) "CHECK AGAIN" else "SCAN NAIL COLOR",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* Gallery */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                 Text(
                    text = "Select from Gallery",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NailCheckPreview() {
    SentiguardTheme {
        NailCheckScreen(
            state = NailCheckState(),
            onEvent = {}
        )
    }
}
