package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    object SimulateGasLeak : SettingsEvent()
    object SimulateHazard : SettingsEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onBack: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Monitoring Preferences Card
            SettingsCard(title = "Monitoring Preferences", icon = Icons.Default.Menu) {
                SettingSwitchItem(
                    label = "Audio Alerts", 
                    desc = "Enable sound notifications for safety alerts", 
                    checked = state.isAudioSupportEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.ToggleAudioSupport(it)) }
                )
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f)))
                SettingSwitchItem(
                    label = "Vibration", 
                    desc = "Enable vibration for critical alerts", 
                    checked = state.isVibrationEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.ToggleVibration(it)) }
                )
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f)))
                SettingSwitchItem("GPS Tracking", "Continuous location monitoring for safety logs", true)
            }

            // Notifications Card
            SettingsCard(title = "Notifications", icon = Icons.Default.Notifications) {
                 SettingSwitchItem("Push Notifications", "Receive alerts on your device", true)
                 Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f)))
                 SettingSwitchItem("Notification Sound", "Play sound for incoming notifications", true)
                 Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f)))
                 SettingSwitchItem("Emergency Alerts", "High-priority safety notifications", true)
            }
            
             // App Lock Placeholder
             SettingsCard(title = "Security", icon = Icons.Default.Lock) {
                 SettingSwitchItem("App Lock (PIN)", "Require PIN to access app", false)
            }

            // Developer Options (Simulation)
            SettingsCard(title = "Developer Tools", icon = Icons.Default.Build) {
                Text(
                    "Simulation Controls", 
                    style = MaterialTheme.typography.labelMedium, 
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = { onEvent(SettingsEvent.SimulateGasLeak) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Simulate Gas Leak", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onEvent(SettingsEvent.SimulateHazard) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberLight, contentColor = AmberDark),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Simulate Acoustic Hazard", fontWeight = FontWeight.SemiBold)
                }
            }
            
            // About Section
            SettingsCard(title = "About", icon = Icons.Default.Info) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Version", style = MaterialTheme.typography.bodyMedium)
                    Text("1.0.0 (Alpha)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Sentiguard: The Digital Life-Shield for Sanitation Workers.\nBuilt with ❤️ by Antigravity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                 Spacer(modifier = Modifier.height(8.dp))
                 val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                 TextButton(
                     onClick = { uriHandler.openUri("https://www.google.com") },
                     contentPadding = PaddingValues(0.dp)
                 ) {
                     Text("Privacy Policy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                 }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(RedPrimary.copy(alpha=0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                     Icon(icon, null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                }
               
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = RedPrimary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
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
