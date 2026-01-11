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
        containerColor = BlackPrimary,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Configuration Section
            SettingItem(
                label = "Vibration Alerts",
                checked = state.isVibrationEnabled,
                onCheckedChange = { onEvent(SettingsEvent.ToggleVibration(it)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingItem(
                label = "Audio Support (\"You are not alone\")",
                checked = state.isAudioSupportEnabled,
                onCheckedChange = { onEvent(SettingsEvent.ToggleAudioSupport(it)) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Language Section (Simple Placeholder)
            Column {
                 Text(
                    text = "Language",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                 Text(
                    text = state.selectedLanguage, // In real app, this would be a selector
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // About Section
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sentiguard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "v1.0.0 (Safety Critical)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDisabled
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
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
