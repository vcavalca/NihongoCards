package asuka.jyuku.nihongocards

import asuka.jyuku.nihongocards.classes.GameState
import asuka.jyuku.nihongocards.classes.Kana
import asuka.jyuku.nihongocards.classes.Script
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateTest {

    private val deck = listOf(
        Kana("あ", "a", Script.HIRAGANA),
        Kana("カ", "ka", Script.KATAKANA)
    )

    @Test
    fun firstTapFlipsTheCard() {
        val state = GameState(deck).onCardTapped()

        assertTrue(state.isFlipped)
        assertEquals(0, state.index)
        assertEquals("a", state.current.romaji)
    }

    @Test
    fun secondTapMovesToTheNextCardFaceUp() {
        val state = GameState(deck).onCardTapped().onCardTapped()

        assertFalse(state.isFlipped)
        assertEquals(1, state.index)
        assertEquals(2, state.position)
        assertEquals("カ", state.current.character)
    }

    @Test
    fun tappingTheRevealedLastCardFinishesTheGame() {
        val revealedLast = GameState(deck, index = 1, isFlipped = true)
        val state = revealedLast.onCardTapped()

        assertTrue(revealedLast.isLastCard)
        assertTrue(state.isFinished)
        assertEquals(1, state.index)
    }

    @Test
    fun tapsAfterTheEndAreIgnored() {
        val finished = GameState(deck, index = 1, isFlipped = true, isFinished = true)

        assertSame(finished, finished.onCardTapped())
    }

    @Test
    fun fullRoundTakesTwoTapsPerCard() {
        var state = GameState(deck)
        var taps = 0
        while (!state.isFinished) {
            state = state.onCardTapped()
            taps++
        }

        assertEquals(deck.size * 2, taps)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyDeckIsRejected() {
        GameState(emptyList())
    }
}
