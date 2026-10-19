package asuka.jyuku.nihongocards.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import asuka.jyuku.nihongocards.objects.KanaData

/**
 * Keeps the current round across rotation, and SavedStateHandle keeps it if Android
 * kills the process while the app is in the background.
 */
class GameViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    private val _state = MutableLiveData<GameState>()
    val state: LiveData<GameState> get() = _state

    init {
        restore()?.let { _state.value = it }
    }

    fun startIfNeeded(scripts: Set<Script>) {
        if (_state.value == null) newGame(scripts)
    }

    fun restart(scripts: Set<Script>) = newGame(scripts)

    fun onCardTapped() {
        _state.value?.let { update(it.onCardTapped()) }
    }

    private fun newGame(scripts: Set<Script>) {
        update(GameState(deck = KanaData.forScripts(scripts).shuffled()))
    }

    private fun update(state: GameState) {
        _state.value = state
        savedState[KEY_DECK] = ArrayList(state.deck.map { it.character })
        savedState[KEY_INDEX] = state.index
        savedState[KEY_FLIPPED] = state.isFlipped
        savedState[KEY_FINISHED] = state.isFinished
    }

    private fun restore(): GameState? {
        val characters = savedState.get<ArrayList<String>>(KEY_DECK) ?: return null
        val deck = characters.mapNotNull { KanaData.find(it) }
        if (deck.isEmpty() || deck.size != characters.size) return null
        return GameState(
            deck = deck,
            index = (savedState.get<Int>(KEY_INDEX) ?: 0).coerceIn(deck.indices),
            isFlipped = savedState.get<Boolean>(KEY_FLIPPED) ?: false,
            isFinished = savedState.get<Boolean>(KEY_FINISHED) ?: false
        )
    }

    private companion object {
        const val KEY_DECK = "deck"
        const val KEY_INDEX = "index"
        const val KEY_FLIPPED = "flipped"
        const val KEY_FINISHED = "finished"
    }
}
