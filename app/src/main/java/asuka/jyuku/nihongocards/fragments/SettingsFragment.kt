package asuka.jyuku.nihongocards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asuka.jyuku.nihongocards.BuildConfig
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.Script
import asuka.jyuku.nihongocards.classes.applySystemBarsPadding
import asuka.jyuku.nihongocards.databinding.FragmentSettingsBinding
import asuka.jyuku.nihongocards.nihongoApp
import asuka.jyuku.nihongocards.objects.KanaData
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding()

        binding.toolbar.setNavigationOnClickListener {
            val navController = findNavController()
            // Ignores a second quick tap while this screen is already closing.
            if (navController.currentDestination?.id == R.id.settingsFragment) {
                navController.popBackStack()
            }
        }

        setUpSound()
        setUpAlphabets()

        binding.versionValueTextView.text = BuildConfig.VERSION_NAME
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpSound() {
        val musicPlayer = nihongoApp.musicPlayer

        binding.toggleMusicSwitch.isChecked = musicPlayer.isEnabled
        binding.volumeSlider.value = musicPlayer.volume.coerceIn(0, 100).toFloat()
        updateVolumeLabel(musicPlayer.volume)

        binding.toggleMusicSwitch.setOnCheckedChangeListener { _, isChecked ->
            musicPlayer.setEnabled(isChecked)
        }

        binding.volumeSlider.addOnChangeListener { _, value, fromUser ->
            val volume = value.toInt()
            if (fromUser) musicPlayer.setVolume(volume)
            updateVolumeLabel(volume)
        }

        val preferences = nihongoApp.preferences
        binding.autoPronounceSwitch.isChecked = preferences.autoPronounce
        binding.autoPronounceSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.autoPronounce = isChecked
        }
    }

    private fun setUpAlphabets() {
        val selected = nihongoApp.preferences.selectedScripts
        binding.hiraganaCard.isChecked = Script.HIRAGANA in selected
        binding.katakanaCard.isChecked = Script.KATAKANA in selected

        binding.hiraganaCountTextView.text = cardCount(KanaData.hiragana.size)
        binding.katakanaCountTextView.text = cardCount(KanaData.katakana.size)

        binding.hiraganaCard.setOnClickListener { onAlphabetTapped(binding.hiraganaCard) }
        binding.katakanaCard.setOnClickListener { onAlphabetTapped(binding.katakanaCard) }
    }

    private fun onAlphabetTapped(card: MaterialCardView) {
        card.toggle()
        val selected = buildSet {
            if (binding.hiraganaCard.isChecked) add(Script.HIRAGANA)
            if (binding.katakanaCard.isChecked) add(Script.KATAKANA)
        }
        if (selected.isEmpty()) {
            // At least one alphabet must stay selected: undo the change.
            card.isChecked = true
            Snackbar.make(binding.root, R.string.at_least_one_alphabet, Snackbar.LENGTH_SHORT).show()
            return
        }
        nihongoApp.preferences.selectedScripts = selected
    }

    private fun cardCount(count: Int): String =
        resources.getQuantityString(R.plurals.deck_card_count, count, count)

    private fun updateVolumeLabel(volume: Int) {
        binding.volumeValueTextView.text = getString(R.string.volume_value_format, volume)
    }
}
