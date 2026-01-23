package com.sentiguard.app.system.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import com.sentiguard.app.R // Updated to match namespace

class MonitoringService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        
        when (action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP -> stopMonitoring()
            ACTION_SIMULATE_GAS -> triggerAlert("Gas Leak (Simulated)")
            ACTION_SIMULATE_HAZARD -> triggerAlert("Acoustic Hazard (Simulated)")
        }

        return START_STICKY
    }

    companion object {
        const val CHANNEL_ID = "MonitoringServiceChannel"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_SIMULATE_GAS = "ACTION_SIMULATE_GAS"
        const val ACTION_SIMULATE_HAZARD = "ACTION_SIMULATE_HAZARD"
    }

    private lateinit var hazardDetector: com.sentiguard.app.system.ml.HazardDetector
    private lateinit var audioStreamProvider: com.sentiguard.app.system.audio.AudioStreamProvider
    private lateinit var audioPlayer: com.sentiguard.app.system.audio.AudioPlayer
    private lateinit var alertManager: com.sentiguard.app.system.alert.AlertManager
    private lateinit var locationManager: com.sentiguard.app.system.location.LocationManager
    private lateinit var repository: com.sentiguard.app.domain.repository.EvidenceRepository
    private lateinit var bleManager: com.sentiguard.app.system.ble.BleManager
    private val serviceScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        audioStreamProvider = com.sentiguard.app.system.audio.AudioStreamProvider(this)
        audioPlayer = com.sentiguard.app.system.audio.AudioPlayer(this)
        alertManager = com.sentiguard.app.system.alert.AlertManager(this)
        locationManager = com.sentiguard.app.system.location.LocationManager(this)
        
        val db = com.sentiguard.app.data.local.db.SentiguardDatabase.getDatabase(this)
        repository = com.sentiguard.app.data.local.LocalEvidenceRepository(db.sessionDao(), db.evidenceDao())
        bleManager = com.sentiguard.app.system.ble.BleManager(this)

        hazardDetector = com.sentiguard.app.system.ml.HazardDetector(this)
        serviceScope.launch {
            hazardDetector.initialize()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Monitoring Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startMonitoring() {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafeGuard Active")
            .setContentText("Monitoring: Audio & Gas Sensors")
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= 29) {
                 startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
            } else {
                 startForeground(1, notification)
            }
        } catch (e: Exception) {
            android.util.Log.e("MonitoringService", "Failed to start foreground service", e)
            stopSelf()
            return // Explicitly return
        }
        
        // Audio Monitoring
        try {
            if (androidx.core.app.ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.RECORD_AUDIO
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                audioStreamProvider.startStreaming { buffer ->
                    try {
                        val result = hazardDetector.analyze(buffer)
                        if (result.detected) {
                            triggerAlert(result.label)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MonitoringService", "Error during audio analysis", e)
                    }
                }
            } else {
                 android.util.Log.e("MonitoringService", "RECORD_AUDIO permission missing")
            }
        } catch (e: Exception) {
            android.util.Log.e("MonitoringService", "Failed to start audio streaming", e)
            stopSelf()
        }

        // BLE Gas Monitoring
        serviceScope.launch {
            try {
                 if (Build.VERSION.SDK_INT >= 31 && 
                    androidx.core.app.ActivityCompat.checkSelfPermission(
                        this@MonitoringService,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    android.util.Log.e("MonitoringService", "BLUETOOTH_CONNECT permission missing")
                     return@launch
                 }
                 
                bleManager.scanAndConnect().collect { gasLevel ->
                    if (gasLevel > 400f) { // Arbitrary threshold for MQ-2 (e.g. 400ppm)
                        triggerAlert("High Gas Concentration: ${gasLevel.toInt()} ppm")
                    }
                }
            } catch (e: Exception) {
                 android.util.Log.e("MonitoringService", "BLE Monitoring failed", e)
            }
        }
        
        scheduleSync()
    }

    private fun scheduleSync() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()
            
        val syncRequest = androidx.work.PeriodicWorkRequestBuilder<com.sentiguard.app.system.sync.SyncWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        ).setConstraints(constraints).build()
        
        try {
            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "SurfaceSync",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        } catch (e: Exception) {
            android.util.Log.e("MonitoringService", "Failed to schedule sync", e)
        }
    }

    private fun triggerAlert(hazardType: String) {
        val notification = alertManager.triggerDangerAlert(hazardType, 2)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(2, notification)
        
        serviceScope.launch {
            try {
                // Permission Check for Location
                if (androidx.core.app.ActivityCompat.checkSelfPermission(
                        this@MonitoringService,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    androidx.core.app.ActivityCompat.checkSelfPermission(
                         this@MonitoringService,
                         android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    val location = locationManager.getCurrentLocation()
                    val event = com.sentiguard.app.domain.model.EvidenceEvent(
                        id = java.util.UUID.randomUUID().toString(),
                        sessionId = "auto_session",
                        timestamp = java.time.LocalDateTime.now(),
                        type = com.sentiguard.app.domain.model.EventType.USER_ALERT,
                        riskLevel = com.sentiguard.app.domain.model.RiskLevel.CRITICAL,
                        latitude = location?.latitude,
                        longitude = location?.longitude,
                        sensorValue = hazardType,
                        data = emptyMap()
                    )
                    repository.logEvent(event)
                } else {
                     // Log detection without location
                     val event = com.sentiguard.app.domain.model.EvidenceEvent(
                        id = java.util.UUID.randomUUID().toString(),
                        sessionId = "auto_session",
                        timestamp = java.time.LocalDateTime.now(),
                        type = com.sentiguard.app.domain.model.EventType.USER_ALERT,
                        riskLevel = com.sentiguard.app.domain.model.RiskLevel.CRITICAL,
                        latitude = null,
                        longitude = null,
                        sensorValue = hazardType,
                        data = emptyMap()
                    )
                    repository.logEvent(event)
                }
            } catch (e: Exception) {
                 android.util.Log.e("MonitoringService", "Failed to log event", e)
            }
        }
    }

    private fun stopMonitoring() {
        releaseResources()
        stopSelf()
    }

    private fun releaseResources() {
        try {
            audioStreamProvider.stopStreaming()
            audioPlayer.stop()
            alertManager.stopAlert()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseResources()
        serviceScope.cancel() 
    }
}
