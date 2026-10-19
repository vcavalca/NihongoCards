package asuka.jyuku.nihongocards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import asuka.jyuku.nihongocards.BuildConfig
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.Script
import asuka.jyuku.nihongocards.classes.applySystemBarsPadding
import asuka.jyuku.nihongocards.databinding.FragmentSettingsBinding
import asuka.jyuku.nihongocards.nihongoApp
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding()

        setUpMusic()
        setUpAlphabets()

        binding.versionValueTextView.text = BuildConfig.VERSION_NAME
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpMusic() {
        val musicPlayer = nihongoApp.musicPlayer

        binding.toggleMusicSwitch.isChecked = musicPlayer.isEnabled
        binding.volumeSeekBar.progress = musicPlayer.volume
        updateVolumeLabel(musicPlayer.volume)

        binding.toggleMusicSwitch.setOnCheckedChangeListener { _, isChecked ->
            musicPlayer.setEnabled(isChecked)
        }

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicPlayer.setVolume(progress)
                updateVolumeLabel(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setUpAlphabets() {
        val selected = nihongoApp.preferences.selectedScripts
        binding.hiraganaCheckBox.isChecked = Script.HIRAGANA in selected
        binding.katakanaCheckBox.isChecked = Script.KATAKANA in selected

        binding.hiraganaCheckBox.setOnCheckedChangeListener { box, _ -> onAlphabetsChanged(box) }
        binding.katakanaCheckBox.setOnCheckedChangeListener { box, _ -> onAlphabetsChanged(box) }
    }

    private fun onAlphabetsChanged(changed: CompoundButton) {
        val selected = buildSet {
            if (binding.hiraganaCheckBox.isChecked) add(Script.HIRAGANA)
            if (binding.katakanaCheckBox.isChecked) add(Script.KATAKANA)
        }
        if (selected.isEmpty()) {
            // At least one alphabet must stay selected: undo the change.
            changed.isChecked = true
            Snackbar.make(binding.root, R.string.at_least_one_alphabet, Snackbar.LENGTH_SHORT).show()
            return
        }
        nihongoApp.preferences.selectedScripts = selected
    }

    private fun updateVolumeLabel(volume: Int) {
        binding.volumeValueTextView.text = getString(R.string.volume_value_format, volume)
    }
}
