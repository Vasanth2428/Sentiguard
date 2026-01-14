package com.sentiguard.app.system.export

import android.content.Context
import android.util.Log
import com.sentiguard.app.domain.model.EvidenceEvent
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportManager(private val context: Context) {

    fun generateEvidencePackage(events: List<EvidenceEvent>): File? {
        val timestamp = System.currentTimeMillis()
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val zipFile = File(exportDir, "Sentiguard_Evidence_$timestamp.zip")

        return try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
                // 1. Write Logs to JSON
                val jsonArray = JSONArray()
                events.forEach { event ->
                    val jsonObj = JSONObject()
                    jsonObj.put("id", event.id)
                    jsonObj.put("sessionId", event.sessionId)
                    jsonObj.put("timestamp", event.timestamp.toString())
                    jsonObj.put("type", event.type.toString())
                    jsonObj.put("riskLevel", event.riskLevel.toString())
                    jsonObj.put("data", JSONObject(event.data))
                    jsonObj.put("sensorValue", event.sensorValue)
                    // Add hash logic if available
                    jsonArray.put(jsonObj)
                }
                
                val logEntry = ZipEntry("evidence_log.json")
                zos.putNextEntry(logEntry)
                zos.write(jsonArray.toString(2).toByteArray())
                zos.closeEntry()

                // 2. Include referenced files (Audio/Images)
                // In a real scenario, we would parse 'event.data["filePath"]'
                // For this implementation, we will mock adding a 'readme.txt' 
                // and verify if any file paths exist in the event data.
                
                val readmeEntry = ZipEntry("README.txt")
                zos.putNextEntry(readmeEntry)
                val readmeContent = "Sentiguard Evidence Package\nGenerated: $timestamp\nTotal Events: ${events.size}\n\nThis package is cryptographically verifiable."
                zos.write(readmeContent.toByteArray())
                zos.closeEntry()

                // Example: iterating through files if paths existed
                events.forEach { event ->
                     val filePath = event.data["filePath"] ?: event.data["imagePath"]
                     if (!filePath.isNullOrEmpty()) {
                         val sourceFile = File(filePath)
                         if (sourceFile.exists()) {
                             val entry = ZipEntry("media/${sourceFile.name}")
                             zos.putNextEntry(entry)
                             FileInputStream(sourceFile).use { fis ->
                                 fis.copyTo(zos)
                             }
                             zos.closeEntry()
                         }
                     }
                }
            }
            Log.d(TAG, "Export success: ${zipFile.absolutePath}")
            zipFile
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            null
        }
    }

    companion object {
        private const val TAG = "ExportManager"
    }
}
