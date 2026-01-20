package com.sentiguard.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
<<<<<<< HEAD
import androidx.compose.foundation.shape.RoundedCornerShape
=======
>>>>>>> de0d02b8194591c7a6055614cf152bc427e5ac38
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentiguard.app.ui.theme.GreenSafe
import com.sentiguard.app.ui.theme.GreenLight

@Composable
fun CoughMonitorScreen(onBack: () -> Unit = {}) {
    var isListening by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    // Animation for the "breathing" / listening effect
    val infiniteTransition = rememberInfiniteTransition(label = "listening_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val opacity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "opacity"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
<<<<<<< HEAD
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState)
=======
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
            // Removed verticalArrangement = Arrangement.Center to allow natural scroll flow
>>>>>>> de0d02b8194591c7a6055614cf152bc427e5ac38
        ) {
            // 1. Red Header (Consistency with Dashboard)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
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
                                onClick = onBack,
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Health Monitor",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Analyzing health and environment patterns in real-time to ensure your safety.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                // Status Indicator Card Overlap
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
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(GreenSafe)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Live Monitoring",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "PROTECTED",
                            style = MaterialTheme.typography.labelLarge,
                            color = GreenSafe,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Central Visualizer Section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                // Outer Pulsing Ripples
                Box(
                    modifier = Modifier
                        .size(220.dp * scale)
                        .clip(CircleShape)
                        .background(GreenSafe.copy(alpha = opacity))
                )
                Box(
                    modifier = Modifier
                        .size(170.dp * (scale * 0.9f))
                        .clip(CircleShape)
                        .background(GreenSafe.copy(alpha = opacity + 0.1f))
                )

                // Inner Circle
                Surface(
                    modifier = Modifier.size(140.dp),
                    shape = CircleShape,
                    color = GreenSafe,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                   Column(
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center
                   ) {
                       Icon(
                           imageVector = Icons.Default.Favorite,
                           contentDescription = null,
                           tint = Color.White,
                           modifier = Modifier.size(40.dp)
                       )
                       Spacer(modifier = Modifier.height(4.dp))
                       Text(
                           "LISTENING",
                           color = Color.White,
                           fontWeight = FontWeight.ExtraBold,
                           style = MaterialTheme.typography.labelLarge
                       )
                   }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Technical Stats (Filling the bottom)
            Text(
                text = "Environment Insights",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                MonitorStatCard(
                    icon = Icons.Default.Face,
                    title = "Respiratory Pattern",
                    status = "Normal",
                    value = "14 bpm",
                    color = GreenSafe
                )
                MonitorStatCard(
                    icon = Icons.Default.Notifications,
                    title = "Ambient Noise",
                    status = "Safe",
                    value = "42 dB",
                    color = GreenSafe
                )
                MonitorStatCard(
                    icon = Icons.Default.LocationOn,
                    title = "Air Quality",
                    status = "Excellent",
                    value = "AQI 12",
                    color = GreenSafe
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Detailed Status (Matches Dashboard Card Style)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
               Column(
                   modifier = Modifier.padding(24.dp)
               ) {
                   Text(
                       "System Health",
                       style = MaterialTheme.typography.titleMedium,
                       fontWeight = FontWeight.Bold
                   )
                   Spacer(modifier = Modifier.height(20.dp))
                   StatusRow("Edge Processing", "Active", GreenSafe)
                   HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                   StatusRow("Neural Engine", "Optimized", GreenSafe)
                   HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                   StatusRow("Sensor Fusion", "Synchronized", GreenSafe)
               }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun MonitorStatCard(
    icon: ImageVector,
    title: String,
    status: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GreenLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = GreenSafe, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(status, style = MaterialTheme.typography.bodySmall, color = color)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
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
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
