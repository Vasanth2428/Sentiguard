package com.sentiguard.app.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
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
    onEvent: (NailCheckEvent) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager(context) }
    
    // Permission Handling
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    NailCheckContent(
        state = state,
        onEvent = onEvent,
        isPermissionGranted = cameraPermissionState.status.isGranted,
        cameraManager = cameraManager,
        lifecycleOwner = lifecycleOwner
    )
}

@Composable
fun NailCheckContent(
    state: NailCheckState,
    onEvent: (NailCheckEvent) -> Unit,
    isPermissionGranted: Boolean,
    cameraManager: CameraManager? = null,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0) 
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             Text(
                text = "Nail Color Analysis",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Camera View / Result View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.5f), RoundedCornerShape(24.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (state.capturedImage != null) {
                    // Show Captured Image
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
                    // Show Camera Preview
                    AndroidView(
                        factory = { ctx ->
                            androidx.camera.view.PreviewView(ctx).also { view ->
                                view.scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                                view.implementationMode = androidx.camera.view.PreviewView.ImplementationMode.COMPATIBLE
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { previewView ->
                            if (cameraManager != null && lifecycleOwner != null) {
                                scope.launch {
                                    cameraManager.startCamera(lifecycleOwner, previewView)
                                }
                            }
                        }
                    )
                    
                    // Viewfinder Frame (Red Corners)
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.8f)
                            .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    )
                } else {
                     Text(
                        text = "Camera Permission Required",
                        color = Color.White
                    )
                }
                
                // Instructions / Overlay
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha=0.6f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = if(state.isResultAvailable) "Result: ${state.resultMessage}" else "Position fingernails within frame",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, null) // Scan Icon
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isResultAvailable) "CHECK AGAIN" else "SCAN NAIL COLOR",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Gallery Selection
                val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
                ) { uri: android.net.Uri? ->
                    uri?.let {
                        // Copy URI to temporary file
                        val inputStream = context.contentResolver.openInputStream(it)
                        val tempFile = File(context.cacheDir, "gallery_upload_${System.currentTimeMillis()}.jpg")
                        inputStream?.use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        onEvent(NailCheckEvent.ImageCaptured(tempFile))
                    }
                }
                
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select from Gallery", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
            isPermissionGranted = true, // Mock permissions for preview
            cameraManager = null,
            lifecycleOwner = null
        )
    }
}
