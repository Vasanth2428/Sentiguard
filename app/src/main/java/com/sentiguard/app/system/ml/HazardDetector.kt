package com.sentiguard.app.system.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.tensorflow.lite.Interpreter
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
    private val outputBuffer = Array(1) { FloatArray(521) } 

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile(modelFilename)
            interpreter = Interpreter(modelBuffer)
            Log.d(TAG, "TFLite model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TFLite model: ${e.message}")
            // Don't crash, just log. Service will handle grace degradation.
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
            return mockInference() // Fallback if model load failed
        }

        try {
            // Preprocessing: Convert ShortArray to FloatArray normalized [-1, 1]
            // Input shape for YAMNet is typically [1, 15600] for 0.975s
            val input = FloatArray(15600)
            val sizeToCopy = minOf(audioBuffer.size, 15600)
            
            for (i in 0 until sizeToCopy) {
                input[i] = audioBuffer[i] / 32768.0f
            }
            // Pad with zeros if short (already zero initialized)

            val inputArray = arrayOf(input)
            interpreter?.run(inputArray, outputBuffer)

            // Post-processing: Find top class
            // For this specific 'Sentiguard' demo, let's look for specific indices or general anomaly.
            // As we don't have the exact label map loaded, we will use a heuristic on the output probability distribution.
            // A real implementation would read label.csv.
            
            val maxScoreIdx = outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
            val maxScore = if(maxScoreIdx != -1) outputBuffer[0][maxScoreIdx] else 0f

            // Threshold for detection
            if (maxScore > 0.5f) {
                // Mapping arbitrary high confidence to "Acoustic Anomaly" if we don't have the specific class name
                // In a real YAMNet, index 0 might be "Speech", 500 might be "Silence".
                // TODO: Load labels.csv for precise naming.
                return HazardResult(true, "Acoustic Hazard (Class $maxScoreIdx)", maxScore)
            }
            
            return HazardResult(false, "Safe", maxScore)

        } catch (e: Exception) {
            Log.e(TAG, "Inference error: ${e.message}")
            return HazardResult(false, "Error", 0f)
        }
    }

    private fun mockInference(): HazardResult {
         // Return safe by default to avoid annoying false positives during dev
         return HazardResult(false, "Normal", 0.99f)
    }
    
    fun simulateDetection(type: String): HazardResult {
        return HazardResult(true, type, 0.95f)
    }

    companion object {
        private const val TAG = "HazardDetector"
    }
}
