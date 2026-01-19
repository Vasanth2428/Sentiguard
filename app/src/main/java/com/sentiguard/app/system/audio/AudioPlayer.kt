package com.sentiguard.app.system.audio

import android.content.Context
import android.media.MediaPlayer
import com.sentiguard.app.R  // Updated to match namespace

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    fun play(resourceId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resourceId)
            mediaPlayer?.setOnCompletionListener { 
                stop() 
            }
            isPrepared = true
        }

        if (isPrepared && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }
    
    fun toggle(resourceId: Int): Boolean {
        if (mediaPlayer?.isPlaying == true) {
            pause()
            return false
        } else {
            play(resourceId)
            return true
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }
}
