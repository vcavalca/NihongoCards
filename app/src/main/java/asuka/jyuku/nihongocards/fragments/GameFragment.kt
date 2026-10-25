package asuka.jyuku.nihongocards.fragments

import android.content.ActivityNotFoundException
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.GameState
import asuka.jyuku.nihongocards.classes.GameViewModel
import asuka.jyuku.nihongocards.classes.KanaSpeaker
import asuka.jyuku.nihongocards.classes.applySystemBarsPadding
import asuka.jyuku.nihongocards.classes.labelRes
import asuka.jyuku.nihongocards.databinding.FragmentGameBinding
import asuka.jyuku.nihongocards.nihongoApp
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.R as MaterialR

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels()

    private var endDialog: AlertDialog? = null
    private var isFlipping = false
    private var speaker: KanaSpeaker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding()

        isFlipping = false
        binding.cardView.cameraDistance = CAMERA_DISTANCE * resources.displayMetrics.density
        binding.cardView.setOnClickListener { onCardClicked() }
        binding.closeButton.setOnClickListener { leaveGame() }
        setUpSpeaker()

        viewModel.startIfNeeded(nihongoApp.preferences.selectedScripts)
        viewModel.state.observe(viewLifecycleOwner) { render(it) }
    }

    override fun onStart() {
        super.onStart()
        // The user may come back from downloading the Japanese voice.
        speaker?.recheckVoice()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speaker?.shutdown()
        speaker = null
        binding.cardView.animate().cancel()
        endDialog?.dismiss()
        endDialog = null
        _binding = null
    }

    private fun onCardClicked() {
        val state = viewModel.state.value ?: return
        if (isFlipping || state.isFinished) return

        binding.cardView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        if (state.isFlipped && state.isLastCard) {
            // Last card already revealed: finish without flipping.
            viewModel.onCardTapped()
        } else {
            flipCard {
                viewModel.onCardTapped()
                val revealed = viewModel.state.value
                if (revealed?.isFlipped == true && nihongoApp.preferences.autoPronounce) {
                    speaker?.speak(revealed.current)
                }
            }
        }
    }

    private fun setUpSpeaker() {
        val speaker = KanaSpeaker(requireContext()).also { speaker = it }
        speaker.status.observe(viewLifecycleOwner) { status ->
            with(binding.speakButton) {
                isVisible = status != KanaSpeaker.Status.UNAVAILABLE
                isEnabled = status != KanaSpeaker.Status.LOADING
                // The tint is a single color, so dim the button by hand while it is disabled.
                alpha = if (isEnabled) 1f else DISABLED_ALPHA
            }
        }
        binding.speakButton.setOnClickListener { onSpeakClicked() }
    }

    private fun onSpeakClicked() {
        val speaker = speaker ?: return
        val kana = viewModel.state.value?.current ?: return
        if (speaker.status.value == KanaSpeaker.Status.MISSING_VOICE) {
            Snackbar.make(binding.root, R.string.japanese_voice_missing, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.hintTextView)
                .setAction(R.string.install_voice) { openVoiceInstaller(speaker) }
                .show()
        } else {
            speaker.speak(kana)
        }
    }

    private fun openVoiceInstaller(speaker: KanaSpeaker) {
        try {
            startActivity(speaker.installVoiceIntent())
        } catch (_: ActivityNotFoundException) {
            // The engine has no voice download screen; nothing else we can do here.
        }
    }

    /** Turns the card halfway, swaps its content, then turns it back. */
    private fun flipCard(onHalfway: () -> Unit) {
        isFlipping = true
        val card = binding.cardView
        card.animate()
            .rotationY(90f)
            .setDuration(FLIP_HALF_DURATION_MS)
            .withEndAction {
                onHalfway()
                card.rotationY = -90f
                card.animate()
                    .rotationY(0f)
                    .setDuration(FLIP_HALF_DURATION_MS)
                    .withEndAction { isFlipping = false }
                    .start()
            }
            .start()
    }

    private fun render(state: GameState) {
        with(binding) {
            counterTextView.text = getString(R.string.card_progress, state.position, state.total)
            progressIndicator.max = state.total
            // A card counts as done once its reading has been revealed.
            progressIndicator.setProgressCompat(state.index + if (state.isFlipped) 1 else 0, true)

            // The pile behind the card shrinks as the deck runs out.
            val cardsLeft = state.total - state.position
            deckLayerTop.isVisible = cardsLeft >= 1
            deckLayerBottom.isVisible = cardsLeft >= 2

            scriptTextView.setText(state.current.script.labelRes)
            if (state.isFlipped) {
                cardView.setBackgroundResource(R.drawable.card_answer)
                cardTextView.text = state.current.romaji
                readingTextView.text = state.current.character
                readingTextView.visibility = View.VISIBLE
                val onCard = ContextCompat.getColor(requireContext(), R.color.white)
                paintCard(
                    content = onCard,
                    label = onCard,
                    labelBackground = ContextCompat.getColor(requireContext(), R.color.white_20)
                )
                hintTextView.setText(if (state.isLastCard) R.string.tap_to_finish else R.string.tap_for_next)
            } else {
                cardView.setBackgroundResource(R.drawable.card_front)
                cardTextView.text = state.current.character
                readingTextView.visibility = View.INVISIBLE
                paintCard(
                    content = MaterialColors.getColor(cardView, MaterialR.attr.colorOnSurface),
                    label = MaterialColors.getColor(cardView, MaterialR.attr.colorOnPrimaryContainer),
                    labelBackground = MaterialColors.getColor(cardView, MaterialR.attr.colorPrimaryContainer)
                )
                hintTextView.setText(R.string.tap_to_reveal)
            }
        }

        if (state.isFinished) showEndOfGameDialog()
    }

    private fun paintCard(@ColorInt content: Int, @ColorInt label: Int, @ColorInt labelBackground: Int) {
        with(binding) {
            cardTextView.setTextColor(content)
            readingTextView.setTextColor(content)
            scriptTextView.setTextColor(label)
            scriptTextView.backgroundTintList = ColorStateList.valueOf(labelBackground)
            speakButton.iconTint = ColorStateList.valueOf(label)
            speakButton.backgroundTintList = ColorStateList.valueOf(labelBackground)
        }
    }

    private fun showEndOfGameDialog() {
        if (endDialog?.isShowing == true) return
        endDialog = MaterialAlertDialogBuilder(
            requireContext(),
            MaterialR.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setIcon(R.drawable.ic_trophy)
            .setTitle(R.string.end_game)
            .setMessage(R.string.retry)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.restart(nihongoApp.preferences.selectedScripts)
            }
            .setNegativeButton(R.string.dialog_no) { _, _ -> leaveGame() }
            .setCancelable(false)
            .show()
    }

    private fun leaveGame() {
        val navController = findNavController()
        // Ignores a second quick tap while this screen is already closing.
        if (navController.currentDestination?.id == R.id.gameFragment) {
            navController.popBackStack()
        }
    }

    private companion object {
        const val FLIP_HALF_DURATION_MS = 150L
        const val CAMERA_DISTANCE = 8000f
        const val DISABLED_ALPHA = 0.38f
    }
}
