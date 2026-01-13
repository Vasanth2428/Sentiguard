package com.sentiguard.app.system.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraManager(private val context: Context) {

    private var imageCapture: ImageCapture? = null

    suspend fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) = suspendCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                continuation.resume(Result.success(Unit))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                continuation.resume(Result.failure(exc))
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun captureImage(
        outputDirectory: File,
        onImageCaptured: (Result<File>) -> Unit
    ) {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            "Sentiguard-${System.currentTimeMillis()}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    onImageCaptured(Result.failure(exc))
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onImageCaptured(Result.success(photoFile))
                }
            }
        )
    }

    companion object {
        private const val TAG = "CameraManager"
    }
}
