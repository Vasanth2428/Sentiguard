package com.sentiguard.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    onEvent: (DashboardEvent) -> Unit
) {
    val statusColor by animateColorAsState(
        targetValue = when (state.securityStatus) {
            SecurityStatus.SAFE -> StatusSafe
            SecurityStatus.WARNING -> StatusWarning
            SecurityStatus.DANGER -> StatusDanger
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
            repeatMode = Restart
        ),
        label = "PulseAlpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SENTIGUARD",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.weight(0.5f))

            // 2. Main Status Indicator (The "Eye")
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Pulse Effect
                if (state.isMonitoring) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(pulseScale)
                            .background(statusColor.copy(alpha = 0.3f), CircleShape)
                    )
                }
                
                // Core Circle
                Surface(
                    modifier = Modifier.size(180.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(4.dp, statusColor),
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (state.securityStatus == SecurityStatus.SAFE) Icons.Default.ThumbUp else Icons.Default.Warning,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.securityStatus.label,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status Message
            Text(
                text = state.statusMessage.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = if (state.isMonitoring) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3. Primary Action
            val buttonBrush = Brush.horizontalGradient(
                colors = if (state.isMonitoring) 
                    listOf(StatusDanger, Color(0xFFB71C1C)) 
                else 
                    listOf(StatusSafe, BrandPrimary)
            )

            Button(
                onClick = { onEvent(DashboardEvent.ToggleMonitoring) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(buttonBrush),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isMonitoring) "STOP MONITORING" else "START PROTECTION",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Quick Actions Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Icons.Default.ThumbUp,
                    label = "Nail Check",
                    onClick = { onEvent(DashboardEvent.NavigateToNailCheck) }
                )
                QuickActionItem(
                    icon = Icons.Default.List,
                    label = "Logs",
                    onClick = { onEvent(DashboardEvent.NavigateToLogs) }
                )
                QuickActionItem(
                    icon = Icons.Default.PlayArrow, // Suggesting Guidance
                    label = "Guidance",
                    onClick = { onEvent(DashboardEvent.NavigateToGuidance) }
                )
                QuickActionItem(
                    icon = Icons.Default.Info, // Statistics
                    label = "Stats",
                    onClick = { onEvent(DashboardEvent.NavigateToStatistics) }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
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
        DashboardScreen(
            state = DashboardState(
                isMonitoring = true,
                securityStatus = SecurityStatus.SAFE,
                statusMessage = "Breathing Normal"
            ),
            onEvent = {}
        )
    }
}
