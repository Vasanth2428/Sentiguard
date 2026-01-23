package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceLogsScreen(
    state: EvidenceLogsState,
    exportState: ExportState = ExportState.Idle,
    verificationState: VerificationUiState = VerificationUiState.Idle,
    onLogClick: (String) -> Unit = {},
    onViewStats: () -> Unit = {},
    onExport: () -> Unit = {},
    onExportHandled: () -> Unit = {},
    onVerify: () -> Unit = {},
    onResetVerification: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Verification Dialog usage remains same...
    if (verificationState is VerificationUiState.Verifying) {
         AlertDialog(
            onDismissRequest = {},
            title = { Text("Verifying Blockchain") },
            text = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator() 
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Validating cryptographic signatures...")
                }
            },
            confirmButton = {}
        )
    } else if (verificationState is VerificationUiState.Verified) {
         AlertDialog(
            onDismissRequest = onResetVerification,
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSafe) },
            title = { Text("Integrity Verified") },
            text = { Text("All ${verificationState.totalCount} events are cryptographically consistent. No tampering detected.") },
            confirmButton = {
                TextButton(onClick = onResetVerification) { Text("OK") }
            }
        )
    } else if (verificationState is VerificationUiState.Tampered) {
         AlertDialog(
            onDismissRequest = onResetVerification,
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = RedPrimary) },
            title = { Text("Tampering Detected!") },
            text = { Text("Validation failed at Log Index ${verificationState.failedIndex}. The chain is broken.") },
            confirmButton = {
                TextButton(onClick = onResetVerification) { Text("CLOSE") }
            }
        )
    }
    
    // Handle Export Side Effect
    LaunchedEffect(exportState) {
        if (exportState is ExportState.Success) {
            val file = exportState.file
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context, 
                "${context.packageName}.fileprovider", 
                file
            )
            
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Export Sentiguard Evidence"))
            onExportHandled() 
        }
    }

    if (exportState is ExportState.Loading) {
         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             CircularProgressIndicator()
         }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Evidence Logs", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewStats) {
                        Icon(Icons.Default.Info, contentDescription = "Stats")
                    }
                    IconButton(onClick = onVerify) {
                        Icon(Icons.Default.Lock, contentDescription = "Verify")
                    }
                    IconButton(onClick = onExport) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp), // Extra bottom padding
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            if (state.logs.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize().padding(bottom = 100.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Evidence Collected",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Events will be securely logged here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Secure Blockchain Storage",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }
                items(state.logs) { log ->
                    LogItem(log, onClick = { onLogClick(log.id) })
                }
            }
        }
    }
}

@Composable
fun LogItem(log: LogEntry, onClick: () -> Unit) {
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // Removed border
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            // Colored Status Strip
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(color)
            )
            
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                 Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${log.date} • ${log.time}", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                     Surface(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Text(
                            text = log.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = log.notes.ifEmpty { "No notes provided." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                if (log.location.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = log.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                         Spacer(modifier = Modifier.width(16.dp))
                         Icon(
                            imageVector = Icons.Default.Schedule, // Updated icon
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = log.duration,
                            style = MaterialTheme.typography.labelSmall,
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
