package asuka.jyuku.nihongocards.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NihongoCards)
        enableEdgeToEdge()

        // Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Background music is handled by MusicPlayer (see NihongoCardsApp), so it survives
        // rotation and pauses when the app goes to the background.
    }

}
