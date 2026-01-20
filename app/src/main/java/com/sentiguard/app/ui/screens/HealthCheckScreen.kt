package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sentiguard.app.ui.theme.GreenSafe
import com.sentiguard.app.ui.theme.RedPrimary
import com.sentiguard.app.ui.theme.GreenLight

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
            title = {
                Text(
                    if (state.isFit) "Ready for Duty" else "Attention Required",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = { 
                Text(
                    if (state.isFit) "Your health screening is complete. You are fit to proceed with your tasks."
                    else "Potential symptoms detected. For your safety, please consult a medical professional or report to your supervisor.",
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.reset(); onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isFit) GreenSafe else RedPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
            icon = {
                Icon(
                    if (state.isFit) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (state.isFit) GreenSafe else RedPrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Red Header Block (Branding Consistency)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.primary)
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
                                onClick = onNavigateBack,
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Health Check",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "A quick screening to ensure you are safe and healthy before starting your work session.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                // Header Card Overlap
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
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Self-Screening Form",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Answer honestly for accurate safety monitoring",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Form Sections
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Symptoms Section
                HealthSectionCard(title = "Symptom Checklist") {
                    SymptomItem(
                        text = "Do you feel dizzy or lightheaded?",
                        icon = Icons.Default.Face,
                        checked = state.hasDizziness,
                        onCheckedChange = { viewModel.onDizzinessChange(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    SymptomItem(
                        text = "Do you have a persistent cough?",
                        icon = Icons.Default.Warning,
                        checked = state.hasCough,
                        onCheckedChange = { viewModel.onCoughChange(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    SymptomItem(
                        text = "Do you feel feverish or have chills?",
                        icon = Icons.Default.Info,
                        checked = state.hasFever,
                        onCheckedChange = { viewModel.onFeverChange(it) }
                    )
                }

                // Vitals Section
                HealthSectionCard(title = "Vital Statistics") {
                    Text(
                        text = "Body Temperature (°C)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.temperatureInput,
                        onValueChange = { viewModel.onTemperatureChange(it) },
                        placeholder = { Text("e.g. 36.5") },
                        leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            "Safe Range: 36.1°C - 37.2°C",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = { viewModel.submitCheck() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = state.temperatureInput.isNotEmpty(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "SUBMIT HEALTH CHECK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun HealthSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun SymptomItem(text: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenSafe
            )
        )
    }
}
