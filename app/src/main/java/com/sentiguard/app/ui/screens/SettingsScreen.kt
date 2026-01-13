package com.sentiguard.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

data class SettingsState(
    val isVibrationEnabled: Boolean = true,
    val isAudioSupportEnabled: Boolean = true,
    val selectedLanguage: String = "English"
)

sealed class SettingsEvent {
    data class ToggleVibration(val enabled: Boolean) : SettingsEvent()
    data class ToggleAudioSupport(val enabled: Boolean) : SettingsEvent()
    data class SelectLanguage(val language: String) : SettingsEvent()
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, null, tint = RedPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings & Support",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Monitoring Preferences Card
            SettingsCard(title = "Monitoring Preferences", icon = Icons.Default.Menu) {
                SettingSwitchItem("Audio Alerts", "Enable sound notifications for safety alerts", true)
                SettingSwitchItem("Vibration", "Enable vibration for critical alerts", true)
                SettingSwitchItem("GPS Tracking", "Continuous location monitoring for safety logs", true)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Notifications Card
            SettingsCard(title = "Notifications", icon = Icons.Default.Notifications) {
                 SettingSwitchItem("Push Notifications", "Receive alerts on your device", true)
                 SettingSwitchItem("Notification Sound", "Play sound for incoming notifications", true)
                 SettingSwitchItem("Emergency Alerts", "High-priority safety notifications", true)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // App Lock Placeholder
             SettingsCard(title = "Security", icon = Icons.Default.Lock) {
                 SettingSwitchItem("App Lock (PIN)", "Require PIN to access app", false)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SettingSwitchItem(
    label: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = TextGrey)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = RedPrimary,
                checkedTrackColor = RedPrimary.copy(alpha = 0.3f),
                uncheckedThumbColor = TextGrey,
                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SentiguardTheme {
        SettingsScreen(
            state = SettingsState(),
            onEvent = {}
        )
    }
}
