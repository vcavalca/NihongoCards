package asuka.jyuku.nihongocards.objects

object HiraganaAlphabet {

    private var isPlaying = true

    private val alphabet = listOf(
        "あ", "い", "う", "え", "お",
        "か", "き", "く", "け", "こ",
        "さ", "し", "す", "せ", "そ",
        "た", "ち", "つ", "て", "と",
        "な", "に", "ぬ", "ね", "の",
        "は", "ひ", "ふ", "へ", "ほ",
        "ま", "み", "む", "め", "も",
        "や",       "ゆ",      "よ",
        "ら", "り", "る", "れ", "ろ",
        "わ",                  "を",
        "ん"
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