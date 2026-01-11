package com.sentiguard.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import com.sentiguard.app.ui.theme.*

@Composable
fun DashboardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit
) {
    // animated color transition (300ms as per spec)
    val statusColor by animateColorAsState(
        targetValue = when (state.securityStatus) {
            SecurityStatus.SAFE -> StatusSafe
            SecurityStatus.WARNING -> StatusWarning
            SecurityStatus.DANGER -> StatusDanger
        },
        animationSpec = tween(durationMillis = 300),
        label = "StatusColorAnimation"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlackPrimary,
        // We use systemBars insets to avoid overlap with status/nav bars
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Respect system bars
                .padding(16.dp), // Design padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = "Sentiguard",
                style = MaterialTheme.typography.headlineMedium, // Title size
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                // "NOT MONITORING | MONITORING ACTIVE"
                text = if (state.isMonitoring) "MONITORING ACTIVE" else "NOT MONITORING",
                style = MaterialTheme.typography.labelLarge, // Button/Label size
                color = if (state.isMonitoring) statusColor else TextDisabled
            )
        }

        // 2. Status Indicator & Description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f), // Take available space to center vertically
            verticalArrangement = Arrangement.Center
        ) {
            // Status Circle: 45% of screen width
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .aspectRatio(1f)
                    .background(statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // If needed, we can put icon or text inside. 
                // Spec implies the color IS the indicator.
                // We'll overlay the label ("SAFE") for accessibility/clarity as per previous implementation logic
                Text(
                    text = state.securityStatus.label,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Status Description
            Text(
                text = state.statusMessage,
                style = MaterialTheme.typography.displayLarge, // Using Status Hierarchy (Largest) for the description status? 
                // Actually spec says: status_detail (text). 
                // Let's use Title (HeadlineMedium) for the description to be readable but not huge.
                // Wait, "Status Component" -> "States" -> "Label". 
                // "Status Description" -> "Breathing normal".
                // I will use HeadlineMedium for description to be very visible.
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        // 3. Actions Section (Primary + Secondary)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            // Primary Action
            Button(
                onClick = { onEvent(DashboardEvent.ToggleMonitoring) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp), // min height 56dp
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isMonitoring) StatusDanger else StatusSafe,
                    contentColor = TextPrimary
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = if (state.isMonitoring) "STOP MONITORING" else "START MONITORING",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secondary Actions Row (Instrument-style layout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Spread visually
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Helper Composable for Consistency
                DashboardActionButton(
                    label = "NAIL CHECK",
                    icon = androidx.compose.material.icons.Icons.Default.ThumbUp, // Using ThumbUp as proxy for "Check"
                    onClick = { onEvent(DashboardEvent.NavigateToNailCheck) }
                )
                
                DashboardActionButton(
                    label = "LOGS",
                    icon = androidx.compose.material.icons.Icons.Default.List,
                    onClick = { onEvent(DashboardEvent.NavigateToLogs) }
                )
                
                DashboardActionButton(
                    label = "SETTINGS",
                    icon = androidx.compose.material.icons.Icons.Default.Settings,
                    onClick = { onEvent(DashboardEvent.NavigateToSettings) }
                )
            }
        }
    }
}

@Composable
fun DashboardActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            // Ensure minimum touch target size
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp) // Large target
                .background(BlackSecondary, CircleShape) // Dark circle bg
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall, // Use small label
            color = TextSecondary,
            // letterSpacing = 1.sp // Add spacing for "technical" look
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
