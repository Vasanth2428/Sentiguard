package com.sentiguard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentiguard.app.ui.theme.*

data class GuidanceItem(
    val title: String,
    val description: String,
    val isCritical: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidanceScreen(
    onBack: () -> Unit
) {
    val guidanceItems = listOf(
        GuidanceItem(
            "Pre-Entry Check",
            "Always performing a visual inspection and gas test before entering any confined space.",
            isCritical = true
        ),
        GuidanceItem(
            "Proper Gear",
            "Ensure you are wearing your safety harness, gloves, and boots before descent."
        ),
        GuidanceItem(
            "Emergency Signals",
            "Agree on a pull-rope signal with the supervisor outside before entering."
        ),
        GuidanceItem(
            "Breathing Control",
            "If you feel dizzy, stop immediately and signal for extraction. Do not panic."
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Safety Guidance", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Audio Player Section
                AudioPlayerCard()
                
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Standard Protocols",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(guidanceItems) { item ->
                GuidanceCard(item)
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun AudioPlayerCard() {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Remember the player to survive recompositions, but handle cleanup if feasible
    val audioPlayer = remember { com.sentiguard.app.system.audio.AudioPlayer(context) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                 Icon(Icons.Rounded.Headphones, null, tint = MaterialTheme.colorScheme.primary)
            }
           
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Calming Audio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = if(isPlaying) "Playing now..." else "Tap to reduce stress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha=0.7f)
                )
            }
            
            IconButton(
                onClick = { 
                    isPlaying = audioPlayer.toggle(com.sentiguard.app.R.raw.calming_audio) 
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun GuidanceCard(item: GuidanceItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCritical) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                 if (item.isCritical) {
                    Icon(
                        Icons.Default.Close, // Warning icon ideal, but Close works as 'Alert' or 'Stop' metaphor if red
                        contentDescription = "Critical",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (item.isCritical) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isCritical) MaterialTheme.colorScheme.onErrorContainer.copy(alpha=0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
