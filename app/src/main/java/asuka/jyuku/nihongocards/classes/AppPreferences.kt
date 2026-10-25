package asuka.jyuku.nihongocards.classes

import android.content.Context
import androidx.core.content.edit

/**
 * Single place where the app reads and writes its settings.
 */
class AppPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ON, false)
        set(value) = prefs.edit { putBoolean(KEY_MUSIC_ON, value) }

    var musicVolume: Int
        get() = prefs.getInt(KEY_MUSIC_VOLUME, DEFAULT_VOLUME)
        set(value) = prefs.edit { putInt(KEY_MUSIC_VOLUME, value.coerceIn(0, 100)) }

    /** Reads the kana aloud each time a card is flipped to its reading. */
    var autoPronounce: Boolean
        get() = prefs.getBoolean(KEY_AUTO_PRONOUNCE, false)
        set(value) = prefs.edit { putBoolean(KEY_AUTO_PRONOUNCE, value) }

    /** Alphabets used in the game. Never empty: falls back to Hiragana. */
    var selectedScripts: Set<Script>
        get() = buildSet {
            if (prefs.getBoolean(KEY_HIRAGANA, true)) add(Script.HIRAGANA)
            if (prefs.getBoolean(KEY_KATAKANA, false)) add(Script.KATAKANA)
        }.ifEmpty { setOf(Script.HIRAGANA) }
        set(value) {
            require(value.isNotEmpty()) { "At least one alphabet must be selected" }
            prefs.edit {
                putBoolean(KEY_HIRAGANA, Script.HIRAGANA in value)
                putBoolean(KEY_KATAKANA, Script.KATAKANA in value)
            }
        }

    private companion object {
        // Name kept so existing installs keep their music settings.
        const val PREFS_NAME = "music_prefs"
        const val KEY_MUSIC_ON = "music_on"
        const val KEY_MUSIC_VOLUME = "music_volume"
        const val KEY_HIRAGANA = "hiragana_enabled"
        const val KEY_KATAKANA = "katakana_enabled"
        const val KEY_AUTO_PRONOUNCE = "auto_pronounce"
        const val DEFAULT_VOLUME = 50
    }
}
