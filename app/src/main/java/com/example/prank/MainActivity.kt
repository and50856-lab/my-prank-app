package com.example.prank

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var audioManager: AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Разворачиваем поверх блокировки и не даем экрану гаснуть
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_main)
        hideSystemUI()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setMaxVolume()

        // Запуск музыки из res/raw/sound.mp3 в цикле
        mediaPlayer = MediaPlayer.create(this, R.raw.sound).apply {
            isLooping = true
            start()
        }

        // Поток, который постоянно держит громкость на максимуме
        Thread {
            while (isRunning) {
                try {
                    setMaxVolume()
                    Thread.sleep(150)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }.start()

        // Обработка клика по секретной кнопке
        findViewById<View>(R.id.secretButton).setOnClickListener {
            stopPrank()
        }
    }

    private fun setMaxVolume() {
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onBackPressed() {
        // Блокируем кнопку "Назад"
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setMaxVolume() // Перехватываем качельки громкости
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun stopPrank() {
        isRunning = false
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        finish()
    }
}
