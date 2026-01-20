package com.sentiguard.app.ui.screens

import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sentiguard.app.system.camera.CameraManager
import com.sentiguard.app.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

data class NailCheckState(
    val instructions: String = "Place your fingernail under the camera",
    val isResultAvailable: Boolean = false,
    val resultMessage: String = "",
    val capturedImage: File? = null
)

sealed class NailCheckEvent {
    data class ImageCaptured(val file: File) : NailCheckEvent()
    data object Reset : NailCheckEvent()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NailCheckScreen(
    state: NailCheckState,
    onEvent: (NailCheckEvent) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager(context) }
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    NailCheckContent(
        state = state,
        onEvent = onEvent,
        onBack = onBack,
        isPermissionGranted = cameraPermissionState.status.isGranted,
        cameraManager = cameraManager,
        lifecycleOwner = lifecycleOwner
    )
}

@Composable
fun NailCheckContent(
    state: NailCheckState,
    onEvent: (NailCheckEvent) -> Unit,
    onBack: () -> Unit = {},
    isPermissionGranted: Boolean,
    cameraManager: CameraManager? = null,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Animation for the scanning frame
    val infiniteTransition = rememberInfiniteTransition(label = "scan_animation")
    val frameAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. Red Header (Consistency with Dashboard/Monitor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
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
                                text = "Nail Health Analysis",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Scan your fingernails to detect potential oxygen levels or nutritional deficiencies.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                // Info Card Overlap
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if(state.isResultAvailable) "Analysis Complete" else "Align your nail with the red frame",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Immersive Scanner View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (state.capturedImage != null) {
                    val bitmap = remember(state.capturedImage) {
                        BitmapFactory.decodeFile(state.capturedImage.absolutePath)
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Nail",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else if (isPermissionGranted && cameraManager != null && lifecycleOwner != null) {
                    AndroidView(
                        factory = { ctx ->
                            androidx.camera.view.PreviewView(ctx).also { view ->
                                view.scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { previewView ->
                            scope.launch {
                                cameraManager.startCamera(lifecycleOwner, previewView)
                            }
                        }
                    )
                    
                    // Tech-focused Viewfinder Frame
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = frameAlpha),
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        // Corner Accents
                        Box(modifier = Modifier.align(Alignment.TopStart).size(30.dp).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 24.dp, topEnd = 4.dp, bottomStart = 4.dp)))
                        Box(modifier = Modifier.align(Alignment.TopEnd).size(30.dp).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(topEnd = 24.dp, topStart = 4.dp, bottomEnd = 4.dp)))
                        Box(modifier = Modifier.align(Alignment.BottomStart).size(30.dp).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 4.dp, topStart = 4.dp)))
                        Box(modifier = Modifier.align(Alignment.BottomEnd).size(30.dp).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 4.dp, topEnd = 4.dp)))
                    }

                    // Scan Line Animation
                    val scanTransition = rememberInfiniteTransition(label = "scan_line")
                    val scanOffset by scanTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 180f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scan_line"
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(160.dp)
                            .height(2.dp)
                            .offset(y = (scanOffset - 90).dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                } else {
                    Text("Permission required to use camera", color = Color.White)
                }
                
                // Floating Result Badge
                if (state.isResultAvailable) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = GreenSafe,
                        tonalElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("NORMAL", color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Bottom Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isResultAvailable) {
                    Text(
                        text = state.resultMessage.ifEmpty { "Nail color appears healthy. Ensure proper lighting for accurate results." },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Button(
                    onClick = { 
                        if (!state.isResultAvailable) {
                            val outputDir = context.cacheDir 
                            cameraManager?.captureImage(outputDir) { result ->
                                result.onSuccess { file ->
                                    onEvent(NailCheckEvent.ImageCaptured(file))
                                }
                            }
                        } else {
                            onEvent(NailCheckEvent.Reset) 
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(if (state.isResultAvailable) Icons.Default.Refresh else Icons.Default.Search, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (state.isResultAvailable) "SCAN AGAIN" else "START ANALYSIS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(
                    onClick = { /* Gallery Logic */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload from device")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NailCheckPreview() {
    SentiguardTheme {
        NailCheckContent(
            state = NailCheckState(),
            onEvent = {},
            isPermissionGranted = true,
            cameraManager = null,
            lifecycleOwner = null
        )
    }
}
