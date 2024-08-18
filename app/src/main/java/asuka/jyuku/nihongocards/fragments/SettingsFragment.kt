package asuka.jyuku.nihongocards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import asuka.jyuku.nihongocards.BuildConfig
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.activities.MainActivity
import asuka.jyuku.nihongocards.classes.PreferencesManager
import asuka.jyuku.nihongocards.classes.SharedViewModel
import asuka.jyuku.nihongocards.databinding.FragmentSettingsBinding
import asuka.jyuku.nihongocards.objects.HiraganaAlphabet
import asuka.jyuku.nihongocards.objects.KatakanaAlphabet

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesPreferences: PreferencesManager
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // SharedPreferences
        preferencesPreferences = PreferencesManager(requireContext())
        val savedVolume = preferencesPreferences.getVolume()

        // Set volume progress
        val volumeProgress = (savedVolume * 100).toInt()
        binding.volumeSeekBar.progress =volumeProgress
        binding.volumeValueTextView.text =
            getString(R.string.volume_value_format, binding.volumeSeekBar.progress)

        // Update checkbox state
        updateCheckBoxesState()

        // Set listeners for checkboxes
        binding.hiraganaCheckBox.setOnCheckedChangeListener { _, isChecked ->
            HiraganaAlphabet.setPlaying(isChecked)
        }
        binding.katakanaCheckBox.setOnCheckedChangeListener { _, isChecked ->
            KatakanaAlphabet.setPlaying(isChecked)
        }

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                sharedViewModel.setVolume(volume)
                binding.volumeValueTextView.text = getString(R.string.volume_value_format, progress)
                preferencesPreferences.setVolume(volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.versionValueTextView.text = BuildConfig.VERSION_NAME

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBarsInsets.top, 0, 0)
            insets
        }

        return binding.root
    }

    private fun updateCheckBoxesState() {
        binding.hiraganaCheckBox.isChecked = HiraganaAlphabet.isPlaying()
        binding.katakanaCheckBox.isChecked = KatakanaAlphabet.isPlaying()
    }

}