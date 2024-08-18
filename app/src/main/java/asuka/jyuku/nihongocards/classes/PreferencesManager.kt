package asuka.jyuku.nihongocards.classes

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    companion object {
        private const val VOLUME_KEY = "volume"
        private const val DEFAULT_VOLUME = 0.5f
    }

    fun getVolume(): Float {
        return preferences.getFloat(VOLUME_KEY, DEFAULT_VOLUME)
    }

    fun setVolume(volume: Float) {
        preferences.edit().putFloat(VOLUME_KEY, volume).apply()
    }

}