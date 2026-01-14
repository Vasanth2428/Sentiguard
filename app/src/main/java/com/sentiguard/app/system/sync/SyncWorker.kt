package com.sentiguard.app.system.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.sentiguard.app.data.local.db.SentiguardDatabase
import com.sentiguard.app.system.security.EncryptionManager
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = SentiguardDatabase.getDatabase(applicationContext)
            val dao = db.evidenceDao()

            // 1. Fetch unsynced data (Mock: we pretend everything older than 1 hour needs sync)
            val events = dao.getAllEventsSync()
            
            Log.d(TAG, "Starting Surface Sync for ${events.size} records...")

            if (events.isNotEmpty()) {
                // 2. Encrypt Data
                encryptData(events)

                // 3. Batch Upload (Simulation)
                delay(2000) 
            }

            Log.d(TAG, "Surface Sync complete. Data strictly protected.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.retry()
        }
    }

    private fun encryptData(events: List<com.sentiguard.app.data.local.db.EvidenceEntity>) {
        try {
            val manager = EncryptionManager()
            
            // Convert to JSON
            val jsonArray = JSONArray()
            events.forEach { event ->
                val jsonObj = JSONObject()
                jsonObj.put("id", event.id)
                jsonObj.put("type", event.type)
                jsonObj.put("risk", event.riskLevel)
                jsonArray.put(jsonObj)
            }
            
            val rawData = jsonArray.toString().toByteArray(Charsets.UTF_8)
            val encrypted = manager.encrypt(rawData)
            
            Log.i(TAG, "Encrypted ${rawData.size} bytes into ${encrypted.size} bytes using AES-256.")
            // In a real app, this 'encrypted' byte array would be sent to the server.
            
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed during sync", e)
            throw e
        }
    }

    companion object {
        const val TAG = "SyncWorker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "SurfaceSync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}
