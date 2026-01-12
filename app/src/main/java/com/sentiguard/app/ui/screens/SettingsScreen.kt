package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    data class SelectLanguage(val language: String) : SettingsEvent() // Placeholder for logic
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.displayMedium, // Massive Header
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Section
            Text("PROFILE", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "Ramesh Kumar", // Mock
                onValueChange = {},
                label = { Text("Worker Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
             OutlinedTextField(
                value = "ID-8829-X", // Mock
                onValueChange = {},
                label = { Text("Worker ID") },
                modifier = Modifier.fillMaxWidth(),
                 colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Emergency Section
            Text("EMERGENCY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
             Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                 Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                     Text("Emergency Contacts", style = MaterialTheme.typography.titleMedium)
                     Spacer(modifier = Modifier.height(8.dp))
                     Text("+91 98765 43210 (Supervisor)", style = MaterialTheme.typography.bodyMedium)
                     Divider(modifier = Modifier.padding(vertical=8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                     Text("108 (Ambulance)", style = MaterialTheme.typography.bodyMedium)
                 }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Configuration Section
            Text("PREFERENCES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            SettingItem(
                label = "Vibration Alerts",
                checked = state.isVibrationEnabled,
                onCheckedChange = { onEvent(SettingsEvent.ToggleVibration(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingItem(
                label = "Audio Support",
                checked = state.isAudioSupportEnabled,
                onCheckedChange = { onEvent(SettingsEvent.ToggleAudioSupport(it)) }
            )
             Spacer(modifier = Modifier.height(16.dp))
             SettingItem(
                label = "App Lock (PIN)",
                checked = false, // Mock
                onCheckedChange = { }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = StatusSafe,
                checkedTrackColor = StatusSafe.copy(alpha = 0.5f),
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = BlackSecondary
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
