package com.sentiguard.app

import android.app.Application
import androidx.room.Room
import com.sentiguard.app.data.local.LocalEvidenceRepository
import com.sentiguard.app.data.local.db.SentiguardDatabase
import com.sentiguard.app.domain.repository.EvidenceRepository

class SentiguardApp : Application() {

    lateinit var database: SentiguardDatabase
        private set

    lateinit var evidenceRepository: EvidenceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Room Database
        // FIX: Use Singleton to prevent duplicate instances/locks
        database = SentiguardDatabase.getDatabase(this)

        // Initialize Repository (Manual DI)
        evidenceRepository = LocalEvidenceRepository(
            sessionDao = database.sessionDao(),
            evidenceDao = database.evidenceDao()
        )
        
        // Schedule Sync
        try {
            com.sentiguard.app.system.sync.SyncWorker.schedule(this)
        } catch (e: Exception) {
            // Safety measure: Don't crash app if scheduling fails
            android.util.Log.e("SentiguardApp", "Failed to schedule background sync", e)
        }
    }
}
