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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    val context = androidx.compose.ui.platform.LocalContext.current
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
    
    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            onEvent(DashboardEvent.ToggleMonitoring)
        } else {
            // Show rationale/alert if any critical permission is denied
            showPermissionRationale = true
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permissions Required") },
            text = { Text("Sentiguard needs Microphone and Location permissions to detect hazards and keep you safe. Please grant them in Settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    // Ideally open App Settings here
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            }
        )
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

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0), // Managed manually for design control
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                        data = android.net.Uri.parse("tel:112")
                    }
                    context.startActivity(intent)
                },
                containerColor = RedPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Call, contentDescription = null) },
                text = { Text("EMERGENCY", fontWeight = FontWeight.Bold) },
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Respect bottom bar
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Red Header Block with Dynamic Insets
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Background Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(RedPrimary, RedDark)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .windowInsetsPadding(WindowInsets.statusBars) // Dynamic status bar padding
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SafeGuard",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "Monitoring Active",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha=0.8f)
                                )
                            }
                            // Profile or Settings Icon placeholder
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(
                                    Icons.Default.AccountCircle, 
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha=0.8f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Cards Container (Overlapping)
                Column(
                    modifier = Modifier
                        .padding(top = 140.dp) // Adjusted for overlap
                        .padding(horizontal = 24.dp) 
                ) {
                    // Start Monitoring Card (Primary Action)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable { 
                                if (state.isMonitoring) {
                                    onEvent(DashboardEvent.ToggleMonitoring)
                                } else {
                                    onRequestPermissions()
                                }
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // More elevation
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 24.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = if (state.isMonitoring) "Monitoring On" else "Start Monitoring",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (state.isMonitoring) GreenSafe else RedPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (state.isMonitoring) "Tap to pause safety scan" else "Tap to begin safety scan",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Animated Play/Stop Button
                            Box(
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .size(72.dp)
                                    .background(
                                        color = if (state.isMonitoring) GreenLight else RedLight,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (state.isMonitoring) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (state.isMonitoring) GreenSafe else RedPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Health Check Button (Secondary)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToHealth() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Status Row (Battery, Network, Session)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCapsule(
                    icon = Icons.Default.BatteryStd, 
                    text = "85%", 
                    color = GreenSafe,
                    modifier = Modifier.weight(1f)
                )
                StatusCapsule(
                    icon = Icons.Default.SignalCellularAlt, 
                    text = "Online", 
                    color = GreenSafe,
                    modifier = Modifier.weight(1f)
                )
                StatusCapsule(
                    icon = Icons.Default.Timer, 
                    text = state.sessionDuration, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Neutral color for time
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Real-Time Status Modules
            Text(
                text = "Live Status",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "Cough Monitor", 
                    subtitle = state.statusMessage, 
                    status = "SAFE", 
                    time = "Running", 
                    icon = Icons.Default.Mic,
                    iconBg = Color(0xFFE1F5FE),
                    iconColor = Color(0xFF0288D1)
                )
                ModuleCard(
                    title = "Heart Rate", 
                    subtitle = "72 BOM", 
                    status = "NORMAL", 
                    time = "1m ago", 
                    icon = Icons.Default.Favorite,
                    iconBg = RedLight,
                    iconColor = RedPrimary
                )
                ModuleCard(
                    title = "Location", 
                    subtitle = "Sector 4, Underground", 
                    status = "TRACKING", 
                    time = "Active", 
                    icon = Icons.Default.LocationOn,
                    iconBg = GreenLight,
                    iconColor = GreenSafe
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Scroll padding
        }
    }
}

@Composable
fun StatusCapsule(
    icon: ImageVector, 
    text: String, 
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.2f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ModuleCard(
    title: String, 
    subtitle: String, 
    status: String, 
    time: String, 
    icon: ImageVector,
    iconBg: Color = MaterialTheme.colorScheme.surfaceVariant,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // Removed border for cleaner look, relying on elevation
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if(status == "SAFE" || status == "NORMAL") GreenLight else AmberLight, 
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = status, 
                        modifier = Modifier.padding(horizontal=8.dp, vertical=4.dp), 
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if(status == "SAFE" || status == "NORMAL") GreenSafe else AmberWarning
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f))
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
                isMonitoring = true,
                securityStatus = SecurityStatus.SAFE,
                statusMessage = "Breathing Normal",
                sessionDuration = "00:45:12"
            ),
            onEvent = {},
            onNavigateToHealth = {},
            onRequestPermissions = {}
        )
    }
}
