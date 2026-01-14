package com.sentiguard.app.system.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat

class AudioStreamProvider(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val sampleRate = 16000 // YAMNet standard
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    fun startStreaming(onBufferAvailable: (ShortArray) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission should be handled by UI before starting service, but safe guard here
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            // Log error or notify failure
            return
        }

        try {
            audioRecord?.startRecording()
            isRecording = true
        } catch (e: Exception) {
            isRecording = false
            return
        }

        Thread {
            val buffer = ShortArray(bufferSize)
            while (isRecording) {
                val readResult = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readResult > 0) {
                    onBufferAvailable(buffer)
                }
            }
        }.start()
    }

    fun stopStreaming() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
    }
}
