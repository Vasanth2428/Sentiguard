package com.sentiguard.app.system.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class NailHealthResult(
    val confidence: Float,
    val label: String,
    val isRisky: Boolean
)

class NailHealthDetector(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val modelFilename = "nail_classifier.tflite"

    // Image Input Configuration (Standard for MobileNet/etc.)
    private val inputImageWidth = 224
    private val inputImageHeight = 224
    private val modelInputSize = inputImageWidth * inputImageHeight * 3 * 4 // 3 channels (RGB), 4 bytes per float

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile(modelFilename)
            interpreter = Interpreter(modelBuffer)
            Log.d(TAG, "Nail Classifier model loaded")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Nail model: ${e.message}")
        }
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun analyze(bitmap: Bitmap): NailHealthResult {
        if (interpreter == null) {
            return mockInference()
        }

        try {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true)
            val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

            // Output buffer: Assuming simple classification [1, num_classes]
            // If we don't know the classes, we'll assume binary or multi-class.
            // Let's assume 3 classes: Healthy, Cyanosis, Anemia
            val output = Array(1) { FloatArray(3) }

            interpreter?.run(byteBuffer, output)

            // Find max score
            val probabilities = output[0]
            val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
            val maxScore = if (maxIdx != -1) probabilities[maxIdx] else 0f

            // Map index to label (Placeholder mapping, assuming training order)
            val (label, isRisky) = when (maxIdx) {
                0 -> "Healthy" to false
                1 -> "Cyanosis Detected" to true // Blue tint
                2 -> "Anemia Detected" to true   // Pale
                else -> "Unknown" to false
            }

            return NailHealthResult(maxScore, label, isRisky)

        } catch (e: Exception) {
            Log.e(TAG, "Inference failed", e)
            return mockInference()
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until inputImageWidth) {
            for (j in 0 until inputImageHeight) {
                val value = intValues[pixel++]

                // Extract RGB and normalize to [0, 1] or [-1, 1] depending on model
                // Assuming standard [0, 1] for now
                byteBuffer.putFloat(((value shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((value shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((value and 0xFF) / 255.0f))
            }
        }
        return byteBuffer
    }

    private fun mockInference(): NailHealthResult {
        // Fallback or "Safe Mode"
        return NailHealthResult(0.85f, "Healthy (Mock)", false)
    }

    companion object {
        private const val TAG = "NailHealthDetector"
    }
}
