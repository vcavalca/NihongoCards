package asuka.jyuku.nihongocards.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _volume = MutableLiveData<Float>()
    val volume: LiveData<Float> get() = _volume

    fun setVolume(volume: Float) {
        _volume.value = volume
    }

}