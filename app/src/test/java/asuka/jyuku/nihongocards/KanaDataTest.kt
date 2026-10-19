package asuka.jyuku.nihongocards

import asuka.jyuku.nihongocards.classes.Script
import asuka.jyuku.nihongocards.objects.KanaData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KanaDataTest {

    @Test
    fun eachScriptHasTheFortySixBasicCharacters() {
        assertEquals(46, KanaData.hiragana.size)
        assertEquals(46, KanaData.katakana.size)
        assertTrue(KanaData.hiragana.all { it.script == Script.HIRAGANA })
        assertTrue(KanaData.katakana.all { it.script == Script.KATAKANA })
    }

    @Test
    fun charactersAndRomajiAreUniqueWithinAScript() {
        for (kana in listOf(KanaData.hiragana, KanaData.katakana)) {
            assertEquals(kana.size, kana.map { it.character }.toSet().size)
            assertEquals(kana.size, kana.map { it.romaji }.toSet().size)
        }
    }

    @Test
    fun hiraganaAndKatakanaShareTheSameReadings() {
        assertEquals(KanaData.hiragana.map { it.romaji }, KanaData.katakana.map { it.romaji })
    }

    @Test
    fun charactersAreInTheExpectedUnicodeBlocks() {
        assertTrue(KanaData.hiragana.all { it.character.single() in '぀'..'ゟ' })
        assertTrue(KanaData.katakana.all { it.character.single() in '゠'..'ヿ' })
    }

    @Test
    fun knownReadings() {
        assertEquals("shi", KanaData.find("し")?.romaji)
        assertEquals("tsu", KanaData.find("ツ")?.romaji)
        assertEquals("n", KanaData.find("ン")?.romaji)
        assertNull(KanaData.find("x"))
    }

    @Test
    fun deckFollowsTheSelectedScripts() {
        assertEquals(KanaData.hiragana, KanaData.forScripts(setOf(Script.HIRAGANA)))
        assertEquals(KanaData.katakana, KanaData.forScripts(setOf(Script.KATAKANA)))
        assertEquals(92, KanaData.forScripts(Script.entries.toSet()).size)
    }
}
