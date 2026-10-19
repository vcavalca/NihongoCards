package asuka.jyuku.nihongocards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.GameState
import asuka.jyuku.nihongocards.classes.GameViewModel
import asuka.jyuku.nihongocards.classes.applySystemBarsPadding
import asuka.jyuku.nihongocards.databinding.FragmentGameBinding
import asuka.jyuku.nihongocards.nihongoApp
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels()

    private var endDialog: AlertDialog? = null
    private var isFlipping = false

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
        binding.cardTextView.cameraDistance = CAMERA_DISTANCE * resources.displayMetrics.density
        binding.cardTextView.setOnClickListener { onCardClicked() }

        viewModel.startIfNeeded(nihongoApp.preferences.selectedScripts)
        viewModel.state.observe(viewLifecycleOwner) { render(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.cardTextView.animate().cancel()
        endDialog?.dismiss()
        endDialog = null
        _binding = null
    }

    private fun onCardClicked() {
        val state = viewModel.state.value ?: return
        if (isFlipping || state.isFinished) return

        if (state.isFlipped && state.isLastCard) {
            // Last card already revealed: finish without flipping.
            viewModel.onCardTapped()
        } else {
            flipCard { viewModel.onCardTapped() }
        }
    }

    /** Turns the card halfway, swaps its content, then turns it back. */
    private fun flipCard(onHalfway: () -> Unit) {
        isFlipping = true
        val card = binding.cardTextView
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
            if (state.isFlipped) {
                cardTextView.text = state.current.romaji
                cardTextView.setBackgroundResource(R.drawable.card_answer)
                hintTextView.setText(if (state.isLastCard) R.string.tap_to_finish else R.string.tap_for_next)
            } else {
                cardTextView.text = state.current.character
                cardTextView.setBackgroundResource(R.drawable.card_front)
                hintTextView.setText(R.string.tap_to_reveal)
            }
        }

        if (state.isFinished) showEndOfGameDialog()
    }

    private fun showEndOfGameDialog() {
        if (endDialog?.isShowing == true) return
        endDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.end_game)
            .setMessage(R.string.retry)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.restart(nihongoApp.preferences.selectedScripts)
            }
            .setNegativeButton(R.string.dialog_no) { _, _ ->
                findNavController().popBackStack()
            }
            .setCancelable(false)
            .show()
    }

    private companion object {
        const val FLIP_HALF_DURATION_MS = 150L
        const val CAMERA_DISTANCE = 8000f
    }
}
