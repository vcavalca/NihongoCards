package asuka.jyuku.nihongocards.classes

import androidx.annotation.StringRes
import asuka.jyuku.nihongocards.R

enum class Script {
    HIRAGANA,
    KATAKANA
}

data class Kana(
    val character: String,
    val romaji: String,
    val script: Script
)

/** Display name of the alphabet, e.g. "Hiragana". */
@get:StringRes
val Script.labelRes: Int
    get() = when (this) {
        Script.HIRAGANA -> R.string.include_hiragana
        Script.KATAKANA -> R.string.include_katakana
    }
