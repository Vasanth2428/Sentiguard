package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    // Verification Dialog
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Creative Header with Gradient and Stats
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                // Background Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(56.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBack,
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Evidence Logs",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }

                        Row {
                            IconButton(onClick = onVerify, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)) {
                                Icon(Icons.Default.Lock, contentDescription = "Verify")
                            }
                            IconButton(onClick = onExport, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)) {
                                Icon(Icons.Default.Share, contentDescription = "Export")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Summary Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryStat(
                            icon = Icons.Default.List,
                            label = "Total Logs",
                            value = state.logs.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryStat(
                            icon = Icons.Default.CheckCircle,
                            label = "Secure",
                            value = "100%",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Filter Card Overlap
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "History Timeline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onViewStats) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Full Report")
                        }
                    }
                }
            }

            if (exportState is ExportState.Loading) {
                 LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.logs.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.logs) { log ->
                        EnhancedLogItem(log, onClick = { onLogClick(log.id) })
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun SummaryStat(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun EnhancedLogItem(log: LogEntry, onClick: () -> Unit) {
    val statusColor = when(log.status) {
        "SAFE" -> GreenSafe
        "WARNING" -> AmberWarning
        "CRITICAL" -> RedPrimary
        else -> MaterialTheme.colorScheme.primary 
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Indicator Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = log.time.take(5), 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = log.date.split("-").lastOrNull() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.status,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = log.notes.ifEmpty { "Incident verification session" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(log.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(log.duration, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun EmptyStateView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(48.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = CircleShape,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Secure Vault Empty",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Evidence logs will be automatically synchronized and stored here after each monitoring session.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
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
                    LogEntry("1", "2023-10-27", "10:00:15", "45m", "SAFE", "Sector 4"),
                    LogEntry("2", "2023-10-26", "14:15:30", "1h 20m", "WARNING", "Sector 2")
                )
            )
        )
    }
}
