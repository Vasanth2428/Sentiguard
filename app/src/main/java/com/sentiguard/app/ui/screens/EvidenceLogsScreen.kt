package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

data class LogEntry(
    val id: String,
    val date: String,
    val time: String,
    val duration: String,
    val status: String,
    val location: String,
    val notes: String = "" // Added for detail view
)

data class EvidenceLogsState(
    val logs: List<LogEntry> = emptyList()
)

@Composable
fun EvidenceLogsScreen(
    state: EvidenceLogsState,
    onLogClick: (String) -> Unit = {},
    onViewStats: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EVIDENCE LOGS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = onViewStats) {
                    Icon(
                        imageVector = Icons.Default.Info, // Using Info or specialized chart icon if available
                        contentDescription = "View Statistics",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "Secure local storage",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.logs) { log ->
                    LogItem(log, onClick = { onLogClick(log.id) })
                }
            }
        }
    }
}

@Composable
fun LogItem(log: LogEntry, onClick: () -> Unit) {
    // Determine color based on status
    val color = when(log.status) {
        "SAFE" -> GreenSafe
        "WARNING" -> AmberWarning
        "CRITICAL" -> RedPrimary
        else -> MaterialTheme.colorScheme.onSurface 
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            // Status Indicator Strip
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(color)
            )
            
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                 Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${log.date} • ${log.time}", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                     Surface(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                         border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha=0.3f))
                    ) {
                        Text(
                            text = log.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = log.notes.ifEmpty { "No notes provided." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (log.location.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, // Red location pin
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = log.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                         Spacer(modifier = Modifier.width(12.dp))
                         Icon(
                            imageVector = Icons.Default.Refresh, // Duration icon proxy
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = log.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
