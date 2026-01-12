package com.sentiguard.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

@Composable
fun StatisticsScreen(
    onBack: () -> Unit
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
                .padding(24.dp)
        ) {
            Text(
                text = "STATISTICS",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem("Hours Monitored", "42.5")
                    StatItem("Sessions", "12")
                    StatItem("Safety Score", "98%")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Weekly Activity",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                SimpleBarChart(
                    data = listOf(0.4f, 0.6f, 0.8f, 0.3f, 0.7f, 0.9f, 0.5f),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // System Health
             Text(
                text = "System Health",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            SystemHealthItem("Microphone Sensitivity", "Optimal", StatusSafe)
            SystemHealthItem("Battery Impact", "Low (Polling 5m)", StatusSafe)
            SystemHealthItem("Storage Space", "1.2GB Free", StatusSafe)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SystemHealthItem(label: String, value: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        Text(text = value, style = MaterialTheme.typography.labelLarge, color = statusColor)
    }
}

@Composable
fun SimpleBarChart(data: List<Float>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = size.width / (data.size * 2)
        val maxBarHeight = size.height

        data.forEachIndexed { index, value ->
            val barHeight = maxBarHeight * value
            drawRect(
                color = color,
                topLeft = Offset(
                    x = (index * 2 * barWidth) + (barWidth / 2),
                    y = maxBarHeight - barHeight
                ),
                size = Size(barWidth, barHeight)
            )
        }
    }
}
