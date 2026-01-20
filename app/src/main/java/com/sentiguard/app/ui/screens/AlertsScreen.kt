package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentiguard.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    state: AlertsState = AlertsState(),
    onEvent: (AlertsEvent) -> Unit = {},
    onBack: () -> Unit = {}
) {
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
            // 1. Vibrant Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(RedPrimary, RedPrimary.copy(alpha = 0.8f))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(56.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onBack,
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Security Alerts",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Real-time safety notifications and historical incident reports.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                // Tab Switcher Overlap
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabButton(
                            label = "ACTIVE",
                            count = state.activeAlerts.size,
                            isSelected = state.selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            activeColor = RedPrimary,
                            onClick = { onEvent(AlertsEvent.SelectTab(0)) }
                        )
                        TabButton(
                            label = "HISTORY",
                            count = state.historicalAlerts.size,
                            isSelected = state.selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            activeColor = MaterialTheme.colorScheme.primary,
                            onClick = { onEvent(AlertsEvent.SelectTab(1)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // List Content
            val alertsToShow = if (state.selectedTab == 0) state.activeAlerts else state.historicalAlerts
            
            if (alertsToShow.isEmpty()) {
                AlertEmptyState(isHistory = state.selectedTab == 1)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(alertsToShow.size) { index ->
                        EnhancedAlertCard(alertsToShow[index], onEvent)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    label: String, 
    count: Int, 
    isSelected: Boolean, 
    activeColor: Color, 
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        color = if (isSelected) activeColor.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count Items",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) activeColor.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun EnhancedAlertCard(alert: Alert, onEvent: (AlertsEvent) -> Unit) {
    val severityColor = when(alert.severity) {
        AlertSeverity.CRITICAL -> RedPrimary
        AlertSeverity.WARNING -> AmberWarning
        else -> MaterialTheme.colorScheme.primary
    }
    
    val backgroundColor = severityColor.copy(alpha = 0.05f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = backgroundColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (alert.severity == AlertSeverity.CRITICAL) Icons.Default.Warning else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        alert.title, 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        alert.timestamp, 
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = severityColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        alert.severity.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = severityColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                alert.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(alert.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            if (alert.status == AlertStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onEvent(AlertsEvent.AcknowledgeAlert(alert.id)) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSafe),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Acknowledge", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onEvent(AlertsEvent.DismissAlert(alert.id)) },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    } 
                }
            }
        }
    }
}

@Composable
fun AlertEmptyState(isHistory: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(48.dp)) {
            Surface(
                color = if(isHistory) MaterialTheme.colorScheme.surfaceVariant else GreenSafe.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if(isHistory) Icons.Default.List else Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = if(isHistory) MaterialTheme.colorScheme.outline else GreenSafe,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if(isHistory) "No Incident History" else "System All Clear",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if(isHistory) "Your historical alert records will appear here once resolved." else "There are currently no active security threats or health alerts detected.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
