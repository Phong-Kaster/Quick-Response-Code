package com.example.quickresponsecode.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.quickresponsecode.R
import java.io.IOException


object SoundUtil {
    private const val TAG = "SoundAndVibrationManager"
    private const val SoundVolume = 0.10f
    private const val VibrationDuration = 200L

    private var isVibrateEnabled: Boolean = true
    private var isSoundEnabled: Boolean = true

    private var lastPlaySound: Long = 0

    /*fun init(settingsRepository: SettingsRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.getVibrateEnabledFlow().collectLatest {
                isVibrateEnabled = it
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.getSoundEnabledFlow().collectLatest {
                isSoundEnabled = it
            }
        }
    }*/

    @Synchronized
    fun vibrateAndRing(context: Context) {
        Log.d(TAG, "vibrateAndRing: ")
        
        if (System.currentTimeMillis() - lastPlaySound < 2000) return

        lastPlaySound = System.currentTimeMillis()
        if (isSoundEnabled) ring(context = context)

        if (isVibrateEnabled) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator2 = vibratorManager.defaultVibrator
                vibrator2.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationDuration)
            }*/

            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationDuration)
        }
    }

    private fun ring(context: Context): MediaPlayer? {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.stop()
            mp.release()
        }
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.w(TAG, "Failed to beep $what, $extra")
            mp.stop()
            mp.release()
            true
        }
        return try {
            val file = context.resources.openRawResourceFd(R.raw.sound_beep)
            file.use { fileUse ->
                mediaPlayer.setDataSource(fileUse.fileDescriptor, fileUse.startOffset, fileUse.length)
            }
            mediaPlayer.setVolume(SoundVolume, SoundVolume)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer
        } catch (ioe: IOException) {
            Log.w(TAG, ioe)
            mediaPlayer.release()
            null
        }
    }
}