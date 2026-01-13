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
        database = Room.databaseBuilder(
            applicationContext,
            SentiguardDatabase::class.java,
            "sentiguard-db"
        ).build()

        // Initialize Repository (Manual DI)
        evidenceRepository = LocalEvidenceRepository(
            sessionDao = database.sessionDao(),
            evidenceDao = database.evidenceDao()
        )
    }
}
