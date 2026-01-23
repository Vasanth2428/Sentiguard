package com.sentiguard.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.GreenSafe

import androidx.compose.material.icons.filled.UploadFile
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.sentiguard.app.system.ml.HazardDetector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoughMonitorScreen(onBack: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isListening by remember { mutableStateOf(true) }
    
    // Check if permission is actually granted
    val hasAudioPermission = remember(context) {
        androidx.core.content.ContextCompat.checkSelfPermission(
            context, 
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // State for file analysis
    var analysisResult by remember { mutableStateOf<String?>(null) }
    var debugInfo by remember { mutableStateOf<String?>(null) }
    var isAnalyzingFile by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Need a stable instance of detector (ideally from DI/ViewModel, creating here for MVP)
    val detector = remember { HazardDetector(context) }
    
    LaunchedEffect(Unit) {
        detector.initialize()
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isAnalyzingFile = true
            analysisResult = "Analyzing file..."
            scope.launch {
                val result = detector.analyzeAudioFile(it)
                analysisResult = if (result.detected) {
                    "HAZARD DETECTED: ${result.label}\nConfidence: ${(result.confidence * 100).toInt()}%"
                } else {
                    "File Analysis: Safe (No Hazards)"
                }
                debugInfo = result.debugDetails
                isAnalyzingFile = false
            }
        }
    }

    // Animation for the "breathing" / listening effect
    val infiniteTransition = rememberInfiniteTransition(label = "listening_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Environmental Monitor", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                text = "Analyzing audio patterns for hazards...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!hasAudioPermission) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Microphone Permission Missing! Detection is disabled.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Visualizer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
            ) {
                // Outer Ripple 2
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(if (isListening) scale * 0.9f else 1f)
                        .clip(CircleShape)
                        .background(GreenSafe.copy(alpha = 0.1f))
                )
                
                // Outer Ripple 1
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(if (isListening) scale else 1f)
                        .clip(CircleShape)
                        .background(GreenSafe.copy(alpha = 0.2f))
                )
                
                // Inner Circle
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(GreenSafe)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                   Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Icon(
                           imageVector = Icons.Default.Mic,
                           contentDescription = null,
                           tint = Color.White,
                           modifier = Modifier.size(56.dp)
                       )
                       Spacer(modifier = Modifier.height(8.dp))
                       Text(
                           "ACTIVE",
                           color = Color.White,
                           fontWeight = FontWeight.Bold,
                           style = MaterialTheme.typography.titleMedium
                       )
                   }
                }
            }



            Spacer(modifier = Modifier.height(32.dp))

            // File Analysis Button
            OutlinedButton(
                onClick = { filePickerLauncher.launch("audio/*") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze Audio File")
            }
            
            if (analysisResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if(analysisResult?.contains("HAZARD") == true) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                         Text(
                            text = "Analysis Result",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysisResult ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Debug Info:", 
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                         Text(
                            text = debugInfo ?: "No debug info",
                            style = MaterialTheme.typography.bodySmall,
                             color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
               Column(
                   modifier = Modifier.padding(20.dp),
                   horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   Text("System Diagnostics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                   Spacer(modifier = Modifier.height(20.dp))
                   StatusRow("Microphone", "Listening", GreenSafe)
                   Spacer(modifier = Modifier.padding(vertical = 12.dp).height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant))
                   StatusRow("ML Model (Edge)", "Running", GreenSafe)
                   Spacer(modifier = Modifier.padding(vertical = 12.dp).height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant))
                   StatusRow("Gas Sensors", "Connected", GreenSafe)
               }
            }
        }
    }
}

@Composable
fun StatusRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                 modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}
