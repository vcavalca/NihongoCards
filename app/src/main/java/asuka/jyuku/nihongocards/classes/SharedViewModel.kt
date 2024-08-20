package asuka.jyuku.nihongocards.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _volume = MutableLiveData<Int>()
    private val _isPlaying = MutableLiveData<Boolean>()

    val volume: LiveData<Int> get() = _volume
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    fun setVolume(volume: Int) {
        _volume.value = volume
    }

    fun setPlaying(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }
}