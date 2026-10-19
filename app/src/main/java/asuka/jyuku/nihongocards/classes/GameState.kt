package asuka.jyuku.nihongocards.classes

/**
 * State of one round. Each card is shown on its front (kana) first; a tap flips it to the
 * back (romaji) and the next tap moves on to the following card.
 */
data class GameState(
    val deck: List<Kana>,
    val index: Int = 0,
    val isFlipped: Boolean = false,
    val isFinished: Boolean = false
) {
    init {
        require(deck.isNotEmpty()) { "Deck must not be empty" }
        require(index in deck.indices) { "Index $index out of bounds" }
    }

    val current: Kana get() = deck[index]
    val position: Int get() = index + 1
    val total: Int get() = deck.size
    val isLastCard: Boolean get() = index == deck.lastIndex

    fun onCardTapped(): GameState = when {
        isFinished -> this
        !isFlipped -> copy(isFlipped = true)
        isLastCard -> copy(isFinished = true)
        else -> copy(index = index + 1, isFlipped = false)
    }
}
