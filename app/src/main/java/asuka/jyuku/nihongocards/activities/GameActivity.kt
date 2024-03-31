package asuka.jyuku.nihongocards.activities

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.databinding.ActivityGameBinding
import asuka.jyuku.nihongocards.objects.HiraganaAlphabet
import asuka.jyuku.nihongocards.objects.KatakanaAlphabet

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var alphabet: List<String>
    private var currentIndex = 0
    private var totalCards = 0
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NihongoCards)
        enableEdgeToEdge()

        // Binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Initialize MusicPlayerManager
        mediaPlayer = MediaPlayer.create(this, R.raw.soudtrack)
        mediaPlayer.isLooping = true
        setVolumeFromPreferences()
        restoreCurrentPosition()
        mediaPlayer.start()

        // Configuration cards
        alphabet = getShuffledAlphabet()
        totalCards = alphabet.size

        showNextCharacter()

        binding.cardTextView.setOnClickListener {
            if (currentIndex < alphabet.size - 1) {
                currentIndex++
                showNextCharacter()
            } else {
                showEndOfGameDialog()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getShuffledAlphabet(): List<String> {
        val hiragana =
            if (HiraganaAlphabet.isPlaying()) HiraganaAlphabet.getShuffledAlphabet() else emptyList()
        val katakana =
            if (KatakanaAlphabet.isPlaying()) KatakanaAlphabet.getShuffledAlphabet() else emptyList()
        return if (hiragana.isNotEmpty() && katakana.isNotEmpty()) {
            shuffleAlphabets(hiragana, katakana)
        } else {
            hiragana + katakana
        }
    }

    private fun showNextCharacter() {
        binding.cardTextView.text = alphabet[currentIndex]
        updateCounterTextView()
    }

    private fun showEndOfGameDialog() {
        AlertDialog.Builder(this).setTitle(getString(R.string.end_game))
            .setMessage(getString(R.string.retry))
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                restartGame()
            }.setNegativeButton(getString(R.string.dialog_no)) { _, _ ->
                finish()
            }.setCancelable(false).show()
    }

    private fun restartGame() {
        currentIndex = 0
        alphabet = getShuffledAlphabet()
        showNextCharacter()
    }

    private fun shuffleAlphabets(hiragana: List<String>, katakana: List<String>): List<String> {
        val combinedList = hiragana + katakana
        return combinedList.shuffled()
    }

    private fun updateCounterTextView() {
        val progress = currentIndex + 1
        binding.counterTextView.text = getString(R.string.card_progress, progress, totalCards)
    }

    private fun setVolumeFromPreferences() {
        val savedVolume = sharedPreferences.getFloat("volume", 0.5f)
        mediaPlayer.setVolume(savedVolume, savedVolume)
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

    private fun restoreCurrentPosition() {
        currentPosition = sharedPreferences.getInt("currentPosition", 0)
        mediaPlayer.seekTo(currentPosition)
    }

}