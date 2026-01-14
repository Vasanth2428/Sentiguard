package com.sentiguard.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

@Composable
fun DashboardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToHealth: () -> Unit = {}
) {
    // Permission State Holder (Stateful)
    val permissions = remember {
        listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ).toMutableList().apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                add(android.Manifest.permission.BLUETOOTH_SCAN)
                add(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            onEvent(DashboardEvent.ToggleMonitoring)
        }
    }

    DashboardContent(
        state = state,
        onEvent = onEvent,
        onNavigateToHealth = onNavigateToHealth,
        onRequestPermissions = { permissionLauncher.launch(permissions.toTypedArray()) }
    )
}

@Composable
fun DashboardContent(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToHealth: () -> Unit,
    onRequestPermissions: () -> Unit
) {
    val statusColor by animateColorAsState(
        targetValue = when (state.securityStatus) {
            SecurityStatus.SAFE -> GreenSafe
            SecurityStatus.WARNING -> AmberWarning
            SecurityStatus.DANGER -> RedPrimary
        },
        animationSpec = tween(durationMillis = 500),
        label = "StatusColor"
    )

    // Pulse Animation for Active Monitoring
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state.isMonitoring) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (state.isMonitoring) 0f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0), // Handle manually
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Emergency Logic */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Call, contentDescription = null) },
                text = { Text("Emergency") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Respect bottom bar
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Red Header Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Background Red
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Spacer(modifier = Modifier.height(32.dp)) // System bar inset
                        Text(
                            text = "SafeGuard Monitor",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Your Safety Command Center",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha=0.9f)
                        )
                    }
                }

                // Daily Health Check Button (New)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                        .clickable { onNavigateToHealth() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Daily Fit-for-Duty Check",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // Overlapping "Start Monitoring" Card
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable { 
                            if (state.isMonitoring) {
                                onEvent(DashboardEvent.ToggleMonitoring)
                            } else {
                                onRequestPermissions()
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Play Button Icon (Red)
                        Icon(
                            imageVector = if (state.isMonitoring) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.isMonitoring) "Monitoring Active" else "Start Monitoring",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (state.isMonitoring) "Tap to stop monitoring" else "Tap to begin safety monitoring",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Status Row (Battery, Network, Timer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusCapsule(icon = Icons.Default.ThumbUp, text = "85%", color = GreenSafe) // Battery proxy
                StatusCapsule(icon = Icons.Default.Send, text = "Online", color = GreenSafe) // Wifi proxy
                StatusCapsule(icon = Icons.Default.Refresh, text = state.sessionDuration, color = RedPrimary) // Timer
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Real-Time Status Modules
            Text(
                text = "Real-Time Status",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ModuleCard(title = "Cough Monitor", subtitle = state.statusMessage, status = "SAFE", time = "5m ago", icon = Icons.Default.Star)
            ModuleCard(title = "Oxygen Level", subtitle = "98%", status = "SAFE", time = "15m ago", icon = Icons.Default.Favorite)
            ModuleCard(title = "GPS Tracking", subtitle = "Active", status = "SAFE", time = "2m ago", icon = Icons.Default.LocationOn)
            
            Spacer(modifier = Modifier.height(100.dp)) // Scroll padding
        }
    }
}

@Composable
fun StatusCapsule(icon: ImageVector, text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.width(100.dp).height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ModuleCard(title: String, subtitle: String, status: String, time: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(GreenLight, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = GreenSafe)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).background(GreenSafe, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = GreenLight, shape = RoundedCornerShape(4.dp)) {
                    Text(status, modifier = Modifier.padding(horizontal=8.dp, vertical=2.dp), style = MaterialTheme.typography.labelSmall, color = GreenSafe)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun RowScope.QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DashboardPreview() {
    SentiguardTheme {
        DashboardContent(
            state = DashboardState(
                isMonitoring = true,
                securityStatus = SecurityStatus.SAFE,
                statusMessage = "Breathing Normal"
            ),
            onEvent = {},
            onNavigateToHealth = {},
            onRequestPermissions = {}
        )
    }
}
