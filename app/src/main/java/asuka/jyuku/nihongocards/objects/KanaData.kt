package asuka.jyuku.nihongocards.objects

import asuka.jyuku.nihongocards.classes.Kana
import asuka.jyuku.nihongocards.classes.Script

object KanaData {

    // Basic gojūon: hiragana, katakana and Hepburn romaji.
    private val gojuon = listOf(
        Triple("あ", "ア", "a"), Triple("い", "イ", "i"), Triple("う", "ウ", "u"),
        Triple("え", "エ", "e"), Triple("お", "オ", "o"),
        Triple("か", "カ", "ka"), Triple("き", "キ", "ki"), Triple("く", "ク", "ku"),
        Triple("け", "ケ", "ke"), Triple("こ", "コ", "ko"),
        Triple("さ", "サ", "sa"), Triple("し", "シ", "shi"), Triple("す", "ス", "su"),
        Triple("せ", "セ", "se"), Triple("そ", "ソ", "so"),
        Triple("た", "タ", "ta"), Triple("ち", "チ", "chi"), Triple("つ", "ツ", "tsu"),
        Triple("て", "テ", "te"), Triple("と", "ト", "to"),
        Triple("な", "ナ", "na"), Triple("に", "ニ", "ni"), Triple("ぬ", "ヌ", "nu"),
        Triple("ね", "ネ", "ne"), Triple("の", "ノ", "no"),
        Triple("は", "ハ", "ha"), Triple("ひ", "ヒ", "hi"), Triple("ふ", "フ", "fu"),
        Triple("へ", "ヘ", "he"), Triple("ほ", "ホ", "ho"),
        Triple("ま", "マ", "ma"), Triple("み", "ミ", "mi"), Triple("む", "ム", "mu"),
        Triple("め", "メ", "me"), Triple("も", "モ", "mo"),
        Triple("や", "ヤ", "ya"), Triple("ゆ", "ユ", "yu"), Triple("よ", "ヨ", "yo"),
        Triple("ら", "ラ", "ra"), Triple("り", "リ", "ri"), Triple("る", "ル", "ru"),
        Triple("れ", "レ", "re"), Triple("ろ", "ロ", "ro"),
        Triple("わ", "ワ", "wa"), Triple("を", "ヲ", "wo"),
        Triple("ん", "ン", "n")
    )

    val hiragana: List<Kana> = gojuon.map { (hira, _, romaji) -> Kana(hira, romaji, Script.HIRAGANA) }
    val katakana: List<Kana> = gojuon.map { (_, kata, romaji) -> Kana(kata, romaji, Script.KATAKANA) }

    private val byCharacter: Map<String, Kana> = (hiragana + katakana).associateBy { it.character }

    fun forScripts(scripts: Set<Script>): List<Kana> = buildList {
        if (Script.HIRAGANA in scripts) addAll(hiragana)
        if (Script.KATAKANA in scripts) addAll(katakana)
    }

    fun find(character: String): Kana? = byCharacter[character]
}
