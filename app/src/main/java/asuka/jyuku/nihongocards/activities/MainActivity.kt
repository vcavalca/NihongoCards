package asuka.jyuku.nihongocards.activities

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.PreferencesManager
import asuka.jyuku.nihongocards.classes.SharedViewModel
import asuka.jyuku.nihongocards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var preferencesPreferences: PreferencesManager
    private val sharedViewModel : SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NihongoCards)
        enableEdgeToEdge()

        // Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences
        preferencesPreferences = PreferencesManager(this)
        val savedVolume = preferencesPreferences.getVolume()

        // Initialize MusicPlayerManager
        mediaPlayer = MediaPlayer.create(this, R.raw.soudtrack).apply {
            isLooping = true
            setVolume(savedVolume, savedVolume)
            start()
        }

        // Observe volume changes
        sharedViewModel.volume.observe(this) { newVolume ->
            mediaPlayer.setVolume(newVolume, newVolume)
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}