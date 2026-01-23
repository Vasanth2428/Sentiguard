package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sentiguard.app.ui.theme.GreenSafe
import com.sentiguard.app.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCheckScreen(
    onNavigateBack: () -> Unit,
    viewModel: HealthCheckViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isSubmitted) {
        AlertDialog(
            onDismissRequest = { viewModel.reset(); onNavigateBack() },
            title = { Text(if (state.isFit) "Fit for Duty" else "Not Fit for Duty", fontWeight = FontWeight.Bold) },
            text = { 
                Text(
                    if (state.isFit) "You have passed the health screening. You may proceed." 
                    else "Critical symptoms detected. Please report to a supervisor immediately.",
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.reset(); onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isFit) GreenSafe else RedPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("DONE", fontWeight = FontWeight.Bold)
                }
            },
            icon = {
                Icon(
                    if (state.isFit) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (state.isFit) GreenSafe else RedPrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daily Health Check", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                 Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Please answer truthfully. Your safety depends on it.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Symptoms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    
                    SymptomRow("Do you feel dizzy?", Icons.Rounded.Face, state.hasDizziness) { viewModel.onDizzinessChange(it) }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    SymptomRow("Persistent cough?", Icons.Rounded.Warning, state.hasCough) { viewModel.onCoughChange(it) }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    SymptomRow("Presence of fever?", Icons.Rounded.ThumbUp, state.hasFever) { viewModel.onFeverChange(it) } 
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Vitals Input", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = state.temperatureInput,
                        onValueChange = { viewModel.onTemperatureChange(it) },
                        label = { Text("Body Temperature (°C)") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Normal range: 36.1°C - 37.2°C", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.submitCheck() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state.temperatureInput.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SUBMIT CHECK", fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SymptomRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
