package asuka.jyuku.nihongocards.objects

object KatakanaAlphabet {

    private var isPlaying = false

    private val alphabet = listOf(
        "ア", "イ", "ウ", "エ", "オ",
        "カ", "キ", "ク", "ケ", "コ",
        "サ", "シ", "ス", "セ", "ソ",
        "タ", "チ", "ツ", "テ", "ト",
        "ナ", "ニ", "ヌ", "ネ", "ノ",
        "ハ", "ヒ", "フ", "ヘ", "ホ",
        "マ", "ミ", "ム", "メ", "モ",
        "ヤ",       "ユ",      "ヨ",
        "ラ", "リ", "ル", "レ", "ロ",
        "ワ",                  "ヲ",
        "ン"
    )

    fun getShuffledAlphabet(): List<String> {
        return alphabet.shuffled()
    }

    fun setPlaying(playing: Boolean) {
        isPlaying = playing
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

}