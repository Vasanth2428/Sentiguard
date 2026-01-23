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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
         topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Safety Alerts", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                     val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = { 
                          android.widget.Toast.makeText(context, "Filter feature coming soon", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Filter")
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
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("Active Alerts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                        Text("$activeCount alerts requiring attention", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha=0.9f))
                                    }
                                }
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
                            }
                        }
                    }
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { onEvent(AlertsEvent.SelectTab(1)) }
                    )
                }
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
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Alerts Found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "You are currently safe.",
                                style = MaterialTheme.typography.bodyMedium,
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
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Medium), 
            color = if(isSelected) color else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(3.dp).background(if(isSelected) color else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(1.dp)))
    }
}

@Composable
fun AlertCard(alert: Alert, onEvent: (AlertsEvent) -> Unit) {
    val cardColor = when(alert.severity) {
        AlertSeverity.CRITICAL -> RedPrimary.copy(alpha=0.08f)
        AlertSeverity.WARNING -> AmberLight.copy(alpha=0.3f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when(alert.severity) {
        AlertSeverity.CRITICAL -> RedPrimary
        AlertSeverity.WARNING -> AmberWarning
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (alert.severity == AlertSeverity.CRITICAL) androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)) else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                 Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = borderColor
                ) {
                    Icon(
                        if (alert.severity == AlertSeverity.CRITICAL) Icons.Default.Warning else Icons.Default.Info, 
                        null, 
                        tint = Color.White, 
                        modifier = Modifier.padding(6.dp).size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(alert.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = borderColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                alert.severity.name, 
                                modifier = Modifier.padding(horizontal=6.dp, vertical=2.dp), 
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), 
                                color = borderColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(alert.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                alert.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
             Spacer(modifier = Modifier.height(8.dp))
             Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    alert.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
             }
            
            
            if (alert.status == AlertStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onEvent(AlertsEvent.AcknowledgeAlert(alert.id)) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = GreenSafe),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ACKNOWLEDGE", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = { onEvent(AlertsEvent.DismissAlert(alert.id)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) { 
                        Icon(Icons.Default.Close, null) 
                    } 
                }
            }
        }
    }
}
