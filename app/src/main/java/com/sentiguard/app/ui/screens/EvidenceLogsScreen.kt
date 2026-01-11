package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

data class LogEntry(
    val id: String,
    val date: String,
    val time: String,
    val duration: String,
    val status: String, // e.g., "SAFE", "WARNING" to map to color
    val location: String
)

data class EvidenceLogsState(
    val logs: List<LogEntry> = emptyList()
)

@Composable
fun EvidenceLogsScreen(
    state: EvidenceLogsState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlackPrimary,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Evidence Logs",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.logs) { log ->
                    LogItem(log)
                }
            }
        }
    }
}

@Composable
fun LogItem(log: LogEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlackSecondary // Dark card on Dark background
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${log.date}   ${log.time}", // Monospace-like spacing
                    style = MaterialTheme.typography.titleMedium, 
                    color = TextPrimary
                )
                // Visual Risk Indicator
                Box(
                    modifier = Modifier
                        .background(
                            color = if (log.status.contains("WARNING", ignoreCase = true)) StatusWarning
                            else if (log.status.contains("DANGER", ignoreCase = true)) StatusDanger
                            else StatusSafe,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = log.status, // "SAFE" or "WARNING"
                        style = MaterialTheme.typography.labelMedium,
                        color = BlackPrimary // Dark text on bright status badge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                 Text(
                    text = "Duration: ${log.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                if (log.location.isNotEmpty()) {
                    Text(
                        text = "Loc: ${log.location}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EvidenceLogsPreview() {
    SentiguardTheme {
        EvidenceLogsScreen(
            state = EvidenceLogsState(
                logs = listOf(
                    LogEntry("1", "2023-10-27", "10:00", "45m", "SAFE", "Sector 4"),
                    LogEntry("2", "2023-10-26", "14:15", "1h 20m", "WARNING", "Sector 2")
                )
            )
        )
    }
}
