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
            throw IllegalStateException("TFLite Interpreter is not initialized. Model likely failed to load.")
        }

        try {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true)
            val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

            // Output buffer: Model outputs 6 classes [1, 6]
            val output = Array(1) { FloatArray(6) }

            interpreter?.run(byteBuffer, output)

            // Find max score
            val probabilities = output[0]
            val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
            val maxScore = if (maxIdx != -1) probabilities[maxIdx] else 0f

            // Map index to label (Updated for 6 classes)
            // Note: Exact labels should be verified with model trainer.
            val (label, isRisky) = when (maxIdx) {
                0 -> "Healthy" to false
                1 -> "Cyanosis" to true       // Class 1
                2 -> "Anemia" to true         // Class 2
                3 -> "Jaundice" to true       // Class 3
                4 -> "Clubbing" to true       // Class 4
                5 -> "Fungal Infection" to true // Class 5
                else -> "Unknown Analysis" to false
            }

            return NailHealthResult(maxScore, label, isRisky)

        } catch (e: Exception) {
            Log.e(TAG, "Inference failed", e)
             throw e
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

                // Extract RGB and normalize to [-1, 1] for MobileNet
                // (value - 127.5) / 127.5
                byteBuffer.putFloat(((value shr 16 and 0xFF) - 127.5f) / 127.5f)
                byteBuffer.putFloat(((value shr 8 and 0xFF) - 127.5f) / 127.5f)
                byteBuffer.putFloat(((value and 0xFF) - 127.5f) / 127.5f)
            }
        }
        return byteBuffer
    }

    companion object {
        private const val TAG = "NailHealthDetector"
    }
}
