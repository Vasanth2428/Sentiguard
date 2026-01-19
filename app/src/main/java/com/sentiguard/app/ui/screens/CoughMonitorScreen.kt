package com.sentiguard.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.GreenSafe

@Composable
fun CoughMonitorScreen(onBack: () -> Unit = {}) {
    var isListening by remember { mutableStateOf(true) }
    
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
            // Removed verticalArrangement = Arrangement.Center to allow natural scroll flow
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Environmental Monitor",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Analyzing audio patterns for hazards...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Visualizer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
            ) {
                // Outer Ripple
                Box(
                    modifier = Modifier
                        .size(200.dp * if (isListening) scale else 1f)
                        .clip(CircleShape)
                        .background(GreenSafe.copy(alpha = 0.2f))
                )
                
                // Inner Circle
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(GreenSafe)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                   Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Icon(
                           imageVector = Icons.Default.Favorite,
                           contentDescription = null,
                           tint = Color.White,
                           modifier = Modifier.size(48.dp)
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

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
               Column(
                   modifier = Modifier.padding(16.dp),
                   horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   Text("System Status", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                   Spacer(modifier = Modifier.height(16.dp))
                   StatusRow("Microphone", "Active", GreenSafe)
                   HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                   StatusRow("ML Model (Edge)", "Running", GreenSafe)
                   HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}
