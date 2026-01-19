package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

@Composable
fun AlertsScreen(
    state: AlertsState = AlertsState(),
    onEvent: (AlertsEvent) -> Unit = {},
    onBack: () -> Unit = {}
) {
    // Whole screen scrolls together
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Notifications, null, tint = RedPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Alerts", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                val context = androidx.compose.ui.platform.LocalContext.current
                IconButton(onClick = { 
                    android.widget.Toast.makeText(context, "Filter feature coming soon", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Menu, null)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // Red Summary Card (Only show if there are active alerts)
            val activeCount = state.activeAlerts.size
            if (activeCount > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RedPrimary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.padding(8.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Active Alerts", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                    Text("$activeCount alerts requiring attention", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha=0.8f))
                                }
                            }
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            // Tabs
            Row(modifier = Modifier.fillMaxWidth()) {
                TabItem(
                    label = "Active (${state.activeAlerts.size})", 
                    isSelected = state.selectedTab == 0, 
                    color = RedPrimary,
                    onClick = { onEvent(AlertsEvent.SelectTab(0)) }
                )
                TabItem(
                    label = "History (${state.historicalAlerts.size})", 
                    isSelected = state.selectedTab == 1, 
                    color = TextGrey,
                    onClick = { onEvent(AlertsEvent.SelectTab(1)) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // List Content
        val alertsToShow = if (state.selectedTab == 0) state.activeAlerts else state.historicalAlerts
        
        if (alertsToShow.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.outline.copy(alpha=0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Alerts Found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "You are currently safe.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
             items(alertsToShow.size) { index ->
                 val alert = alertsToShow[index]
                 AlertCard(alert, onEvent)
             }
             item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun RowScope.TabItem(label: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick), 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label, 
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal), 
            color = if(isSelected) RedPrimary else TextGrey
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(if(isSelected) 2.dp else 1.dp).background(if(isSelected) RedPrimary else DividerColor))
    }
}

@Composable
fun AlertCard(alert: Alert, onEvent: (AlertsEvent) -> Unit) {
    val cardColor = when(alert.severity) {
        AlertSeverity.CRITICAL -> RedPrimary.copy(alpha=0.1f)
        AlertSeverity.WARNING -> AmberLight
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when(alert.severity) {
        AlertSeverity.CRITICAL -> RedPrimary
        AlertSeverity.WARNING -> AmberWarning
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                 Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = borderColor
                ) {
                    Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(alert.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = borderColor, shape = RoundedCornerShape(4.dp)) {
                            Text(alert.severity.name, modifier = Modifier.padding(horizontal=4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(alert.timestamp, style = MaterialTheme.typography.bodySmall, color = TextGrey)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                alert.message,
                style = MaterialTheme.typography.bodyMedium
            )
             Spacer(modifier = Modifier.height(8.dp))
            Text(
                alert.location,
                style = MaterialTheme.typography.bodySmall,
                color = TextGrey
            )
            
            if (alert.status == AlertStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { onEvent(AlertsEvent.AcknowledgeAlert(alert.id)) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenSafe),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenSafe)
                    ) {
                        Text("Acknowledge")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { onEvent(AlertsEvent.DismissAlert(alert.id)) }) { Icon(Icons.Default.Clear, null) } 
                }
            }
        }
    }
}
