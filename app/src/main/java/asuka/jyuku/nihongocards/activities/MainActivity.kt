package asuka.jyuku.nihongocards.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.MediaPlayerManager
import asuka.jyuku.nihongocards.classes.SharedViewModel
import asuka.jyuku.nihongocards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NihongoCards)
        enableEdgeToEdge()

        // Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
        mediaPlayerManager = MediaPlayerManager(this, sharedPreferences)

        sharedViewModel.volume.observe(this, Observer { volume ->
            mediaPlayerManager.setVolume(volume)
        })

        sharedViewModel.isPlaying.observe(this, Observer { isPlaying ->
            if (isPlaying) {
                mediaPlayerManager.playMusic()
            } else {
                mediaPlayerManager.stopMusic()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerManager.release()
    }

}