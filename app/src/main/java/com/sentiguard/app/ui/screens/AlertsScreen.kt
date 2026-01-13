package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, null, tint = RedPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Alerts Management", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
            IconButton(onClick = { /* Filter */ }) {
                Icon(Icons.Default.Menu, null)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Red Summary Card
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
                            Text("1 alert requiring attention", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha=0.8f))
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AlertStatItem(value = "0", label = "Critical")
                    AlertStatItem(value = "1", label = "Warning")
                    AlertStatItem(value = "15m", label = "Oldest")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tabs
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Active (1)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = RedPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(2.dp).background(RedPrimary))
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("History (4)", style = MaterialTheme.typography.titleMedium, color = TextGrey)
                Spacer(modifier = Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Alert Card (Warning)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AmberLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberWarning
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Low Oxygen Level Warning", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = AmberWarning, shape = RoundedCornerShape(4.dp)) {
                                Text("WARNING", modifier = Modifier.padding(horizontal=4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("15m ago", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Nail color analysis indicates moderate oxygen deprivation. Monitor closely.",
                    style = MaterialTheme.typography.bodyMedium
                )
                 Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Sector 7, Manhole Site A",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenSafe),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenSafe)
                    ) {
                        Text("Acknowledge")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = {}) { Icon(Icons.Default.Clear, null) } // Mute/Dismiss
                }
            }
        }
    }
}

@Composable
private fun AlertStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha=0.8f))
    }
}
