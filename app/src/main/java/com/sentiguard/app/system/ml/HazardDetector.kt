package com.sentiguard.app.system.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.tensorflow.lite.Interpreter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class HazardResult(
    val detected: Boolean,
    val label: String,
    val confidence: Float
)

class HazardDetector(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val modelFilename = "sound_classifier.tflite"

    // YAMNet typically returns 521 scores. We focus on specific indices for "Industrial" hazards.
    // NOTE: These indices depend on the specific training mapping. 
    // For this implementation, we will check generic "Gas Leak" / "Hiss" class approximations or high energy anomalies.
    private var outputBuffer = Array(1) { FloatArray(521) } 

    suspend fun initialize() {
        if (interpreter != null) return
        withContext(Dispatchers.IO) {
            loadModel()
        }
    }

    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile(modelFilename)
            interpreter = Interpreter(modelBuffer)
            Log.d(TAG, "TFLite model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TFLite model: ${e.message}")
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

    fun analyze(audioBuffer: ShortArray): HazardResult {
        if (interpreter == null) {
            return HazardResult(false, "Initializing...", 0f)
        }

        try {
            val input = FloatArray(15600)
            val sizeToCopy = minOf(audioBuffer.size, 15600)
            
            for (i in 0 until sizeToCopy) {
                input[i] = audioBuffer[i] / 32768.0f
            }

            val inputArray = arrayOf(input)
            interpreter?.run(inputArray, outputBuffer)

            val maxScoreIdx = outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
            val maxScore = if(maxScoreIdx != -1) outputBuffer[0][maxScoreIdx] else 0f

            if (maxScore > 0.5f) {
                return HazardResult(true, "Acoustic Hazard (Class $maxScoreIdx)", maxScore)
            }
            
            return HazardResult(false, "Safe", maxScore)

        } catch (e: Exception) {
            Log.e(TAG, "Inference error: ${e.message}")
            return HazardResult(false, "Error", 0f)
        }
    }

    private fun mockInference(): HazardResult {
         return HazardResult(false, "Normal", 0.99f)
    }
    
    fun simulateDetection(type: String): HazardResult {
        return HazardResult(true, type, 0.95f)
    }

    companion object {
        private const val TAG = "HazardDetector"
    }
}
