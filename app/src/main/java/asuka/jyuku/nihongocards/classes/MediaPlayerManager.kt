package asuka.jyuku.nihongocards.classes

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import asuka.jyuku.nihongocards.R

class MediaPlayerManager(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private val _isPlaying = MutableLiveData<Boolean>()
    private val _volume = MutableLiveData<Int>()

    val isPlaying: LiveData<Boolean> get() = _isPlaying
    val volume: LiveData<Int> get() = _volume

    init {
        _volume.value = sharedPreferences.getInt("music_volume", 50)
        _isPlaying.value = sharedPreferences.getBoolean("music_on", false)

        mediaPlayer?.setOnCompletionListener {
            _isPlaying.value = false
        }

        if (_isPlaying.value == true) {
            playMusic()
        }
    }

    fun playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer?.apply {
            reset()
            val afd = context.resources.openRawResourceFd(R.raw.soudtrack)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            setVolume(_volume.value!!.toFloat() / 100, _volume.value!!.toFloat() / 100)
            prepare()
            start()
        }
        _isPlaying.value = true
        sharedPreferences.edit().putBoolean("music_on", true).apply()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        _isPlaying.value = false
        sharedPreferences.edit().putBoolean("music_on", false).apply()
    }

    fun setVolume(volume: Int) {
        _volume.value = volume
        mediaPlayer?.setVolume(volume.toFloat() / 100, volume.toFloat() / 100)
        sharedPreferences.edit().putInt("music_volume", volume).apply()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}