package com.sentiguard.app.system.alert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class AlertManager(private val context: Context) {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun triggerDangerAlert(hazardType: String, notificationId: Int): Notification {
        // 1. Vibration (SOS Pattern: ... --- ...)
        val sosTiming = longArrayOf(
            0, 200, 100, 200, 100, 200, // S (...)
            300, 500, 200, 500, 200, 500, // O (---)
            300, 200, 100, 200, 100, 200 // S (...)
        )
        
        // Amplitudes: 0=off, 255=max
        // For array waveform, we need timing and amplitudes (if supported) or just timings (old way)
        // Let's use createWaveform with repeat = -1 (no repeat)
        
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // High API: Create waveform
                 val effect = VibrationEffect.createWaveform(sosTiming, -1)
                 vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(sosTiming, -1)
            }
        }

        // 2. Notification with High Importance
        createHighPriorityChannel()
        
        return NotificationCompat.Builder(context, CHANNEL_ID_DANGER)
            .setContentTitle("DANGER DETECTED: $hazardType")
            .setContentText("Evacuate immediately! High levels detected.")
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Heads-up
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setVibrate(sosTiming) // Fallback for notification vibration
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
    }
    
    fun stopAlert() {
        vibrator.cancel()
        notificationManager.cancelAll() // Or specific ID
    }

    private fun createHighPriorityChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_DANGER,
                "Hazard Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts for gas and health hazards"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_DANGER = "SentiguardHazardChannel"
    }
}
