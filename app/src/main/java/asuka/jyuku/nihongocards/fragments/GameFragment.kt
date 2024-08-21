package asuka.jyuku.nihongocards.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.databinding.FragmentGameBinding
import asuka.jyuku.nihongocards.objects.HiraganaAlphabet
import asuka.jyuku.nihongocards.objects.KatakanaAlphabet

class GameFragment : Fragment(R.layout.fragment_game) {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var alphabet: List<String>
    private var currentIndex = 0
    private var totalCards = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        if (!HiraganaAlphabet.isPlaying() && !KatakanaAlphabet.isPlaying()) {
            HiraganaAlphabet.setPlaying(true)
        }

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBarsInsets.top, 0, 0)
            insets
        }

        return binding.root
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

    private fun shuffleAlphabets(hiragana: List<String>, katakana: List<String>): List<String> {
        val combinedList = hiragana + katakana
        return combinedList.shuffled()
    }

    private fun showNextCharacter() {
        binding.cardTextView.text = alphabet[currentIndex]
        updateCounterTextView()
    }

    private fun updateCounterTextView() {
        val progress = currentIndex + 1
        binding.counterTextView.text = getString(R.string.card_progress, progress, totalCards)
    }

    private fun showEndOfGameDialog() {
        AlertDialog.Builder(context).setTitle(getString(R.string.end_game))
            .setMessage(getString(R.string.retry))
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                restartGame()
            }.setNegativeButton(getString(R.string.dialog_no)) { _, _ ->
                requireActivity().finish()
            }.setCancelable(false).show()
    }

    private fun restartGame() {
        currentIndex = 0
        alphabet = getShuffledAlphabet()
        showNextCharacter()
    }

}