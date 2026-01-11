package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Nail Oxygen Check",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Instructional Text
        Text(
            text = state.instructions,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary, // Secondary for instruction to distinguish from header
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Camera Preview Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(BlackSecondary), // Darker placeholder
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "[ CAMERA PREVIEW ]",
                style = MaterialTheme.typography.bodyLarge,
                color = TextDisabled
            )
        }

        if (state.isResultAvailable) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = state.resultMessage,
                style = MaterialTheme.typography.headlineMedium,
                color = StatusSafe // Assuming safe result for demo, logic would determine this
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Button
        Button(
            onClick = { if (!state.isResultAvailable) onEvent(NailCheckEvent.CaptureImage) else onEvent(NailCheckEvent.Reset) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StatusSafe
            )
        ) {
            Text(
                text = if (state.isResultAvailable) "CHECK AGAIN" else "CAPTURE",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
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
