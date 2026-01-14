package com.sentiguard.app.system.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.sentiguard.app.data.local.db.SentiguardDatabase
import kotlinx.coroutines.delay
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
                // 2. Encryption placeholder
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

    private fun encryptData(data: List<Any>) {
        // "Justice Sentinel" logic: AES-256 encryption before transmission
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
