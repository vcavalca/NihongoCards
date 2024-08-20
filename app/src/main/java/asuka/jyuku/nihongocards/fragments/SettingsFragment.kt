package asuka.jyuku.nihongocards.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import asuka.jyuku.nihongocards.BuildConfig
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.SharedViewModel
import asuka.jyuku.nihongocards.databinding.FragmentSettingsBinding
import asuka.jyuku.nihongocards.objects.HiraganaAlphabet
import asuka.jyuku.nihongocards.objects.KatakanaAlphabet

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
        val isMusicOn = sharedPreferences.getBoolean("music_on", false)
        val volume = sharedPreferences.getInt("music_volume", 50)

        binding.volumeSeekBar.progress = volume
        binding.toggleMusicSwitch.isChecked = isMusicOn
        binding.volumeValueTextView.text = getString(R.string.volume_value_format, volume)

        sharedViewModel.isPlaying.observe(viewLifecycleOwner, Observer { isPlaying ->
            binding.toggleMusicSwitch.isChecked = isPlaying
        })

        sharedViewModel.volume.observe(viewLifecycleOwner, Observer { newVolume ->
            binding.volumeSeekBar.progress = newVolume
            binding.volumeValueTextView.text = getString(R.string.volume_value_format, newVolume)
        })

        binding.toggleMusicSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setPlaying(isChecked)
        }

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sharedViewModel.setVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update checkbox state
        updateCheckBoxesState()

        // Set listeners for checkboxes
        binding.hiraganaCheckBox.setOnCheckedChangeListener { _, isChecked ->
            HiraganaAlphabet.setPlaying(isChecked)
        }
        binding.katakanaCheckBox.setOnCheckedChangeListener { _, isChecked ->
            KatakanaAlphabet.setPlaying(isChecked)
        }

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