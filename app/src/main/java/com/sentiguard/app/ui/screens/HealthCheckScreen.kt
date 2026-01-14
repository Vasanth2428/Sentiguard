package com.sentiguard.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
            title = { Text(if (state.isFit) "Fit for Duty" else "Not Fit for Duty") },
            text = { 
                Text(
                    if (state.isFit) "You have passed the health screening. You may proceed." 
                    else "Critical symptoms detected. Please report to a supervisor immediately."
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.reset(); onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isFit) GreenSafe else RedPrimary
                    )
                ) {
                    Text("Done")
                }
            },
            icon = {
                Icon(
                    if (state.isFit) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (state.isFit) GreenSafe else RedPrimary
                )
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daily Health Check") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Please answer truthfully correctly.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Symptoms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    
                    SymptomRow("Do you feel dizzy?", state.hasDizziness) { viewModel.onDizzinessChange(it) }
                    Divider(Modifier.padding(vertical = 8.dp))
                    SymptomRow("Do you have a persistent cough?", state.hasCough) { viewModel.onCoughChange(it) }
                    Divider(Modifier.padding(vertical = 8.dp))
                    SymptomRow("Do you have a fever?", state.hasFever) { viewModel.onFeverChange(it) }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Vitals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = state.temperatureInput,
                        onValueChange = { viewModel.onTemperatureChange(it) },
                        label = { Text("Body Temperature (°C)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Normal range: 36.1°C - 37.2°C", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.submitCheck() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = state.temperatureInput.isNotEmpty()
            ) {
                Text("Submit Health Check")
            }
        }
    }
}

@Composable
fun SymptomRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
