package asuka.jyuku.nihongocards.classes

enum class Script {
    HIRAGANA,
    KATAKANA
}

data class Kana(
    val character: String,
    val romaji: String,
    val script: Script
)
