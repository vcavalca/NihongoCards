package asuka.jyuku.nihongocards.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NihongoCards)
        enableEdgeToEdge()

        // Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Initialize MusicPlayerManager
        mediaPlayer = MediaPlayer.create(this, R.raw.soudtrack)
        mediaPlayer.isLooping = true
        setVolumeFromPreferences()
        restoreCurrentPosition()
        mediaPlayer.start()

        binding.startButton.setOnClickListener {
            saveCurrentPosition()
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra("currentPosition", mediaPlayer.currentPosition)
            })
        }

        binding.settingsButton.setOnClickListener {
            saveCurrentPosition()
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                putExtra("currentPosition", mediaPlayer.currentPosition)
            })
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        setVolumeFromPreferences()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
    }

    private fun setVolumeFromPreferences() {
        val savedVolume = sharedPreferences.getFloat("volume", 0.5f)
        mediaPlayer.setVolume(savedVolume, savedVolume)
    }

    private fun saveCurrentPosition() {
        val currentPosition = mediaPlayer.currentPosition
        sharedPreferences.edit().putInt("currentPosition", currentPosition).apply()
    }

    private fun restoreCurrentPosition() {
        currentPosition = sharedPreferences.getInt("currentPosition", 0)
        mediaPlayer.seekTo(currentPosition)
    }

}