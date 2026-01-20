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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0),
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
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Red Header Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp) 
            ) {
                // Background Red
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Text(
                            text = "Sentiguard",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your Safety Command Center",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha=0.8f),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(40.dp))

                        // Daily Health Check Button
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToHealth() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp, horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle, 
                                    contentDescription = null, 
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Daily Fit-for-Duty Check",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }

                // Overlapping "Start Monitoring" Card
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(160.dp) // Increased height for better inner padding
                        .clickable { 
                            if (state.isMonitoring) {
                                onEvent(DashboardEvent.ToggleMonitoring)
                            } else {
                                onRequestPermissions()
                            }
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp), // Explicit inner padding
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (state.isMonitoring) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp) // Slightly larger icon
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (state.isMonitoring) "Monitoring Active" else "Start Monitoring",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Added spacer for subtitle
                        Text(
                            text = if (state.isMonitoring) "Tap to stop monitoring" else "Tap to begin safety monitoring",
                            style = MaterialTheme.typography.bodyMedium, // Larger subtitle for readability
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Status Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCapsule(modifier = Modifier.weight(1f), icon = Icons.Default.ThumbUp, text = "85%", color = GreenSafe)
                StatusCapsule(modifier = Modifier.weight(1f), icon = Icons.Default.Send, text = "Online", color = GreenSafe)
                StatusCapsule(modifier = Modifier.weight(1f), icon = Icons.Default.Refresh, text = state.sessionDuration, color = RedPrimary)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Real-Time Status Modules
            Text(
                text = "Real-Time Status",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ModuleCard(title = "Cough Monitor", subtitle = state.statusMessage, status = "SAFE", time = "5m ago", icon = Icons.Default.Star)
            ModuleCard(title = "Oxygen Level", subtitle = "98%", status = "SAFE", time = "15m ago", icon = Icons.Default.Favorite)
            ModuleCard(title = "GPS Tracking", subtitle = "Active", status = "SAFE", time = "2m ago", icon = Icons.Default.LocationOn)
            
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun StatusCapsule(modifier: Modifier = Modifier, icon: ImageVector, text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ModuleCard(title: String, subtitle: String, status: String, time: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(GreenLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = GreenSafe, modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(GreenSafe, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = GreenLight, shape = RoundedCornerShape(8.dp)) {
                    Text(status, modifier = Modifier.padding(horizontal=10.dp, vertical=4.dp), style = MaterialTheme.typography.labelMedium, color = GreenSafe)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DashboardPreview() {
    SentiguardTheme {
        DashboardContent(
            state = DashboardState(
                isMonitoring = false,
                securityStatus = SecurityStatus.SAFE,
                statusMessage = "System Ready"
            ),
            onEvent = {},
            onNavigateToHealth = {},
            onRequestPermissions = {}
        )
    }
}
