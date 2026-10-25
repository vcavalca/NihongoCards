package asuka.jyuku.nihongocards.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.applySystemBarsPadding
import asuka.jyuku.nihongocards.classes.labelRes
import asuka.jyuku.nihongocards.databinding.FragmentMainBinding
import asuka.jyuku.nihongocards.nihongoApp
import asuka.jyuku.nihongocards.objects.KanaData
import com.google.android.material.transition.MaterialSharedAxis

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var mascotAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Material shared axis motion; it also follows the predictive back gesture.
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding()

        showDeckSummary()
        startMascotAnimation()

        binding.startButton.setOnClickListener {
            navigate(R.id.action_mainFragment_to_gameFragment)
        }
        binding.settingsButton.setOnClickListener {
            navigate(R.id.action_mainFragment_to_settingsFragment)
        }
        binding.deckCard.root.setOnClickListener {
            navigate(R.id.action_mainFragment_to_settingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mascotAnimator?.cancel()
        mascotAnimator = null
        _binding = null
    }

    /** Shows which alphabets the next game will use and how many cards that is. */
    private fun showDeckSummary() {
        val scripts = nihongoApp.preferences.selectedScripts
        val deck = KanaData.forScripts(scripts)
        with(binding.deckCard) {
            // First kana of each alphabet: "あ", "ア" or "あア".
            deckBadgeTextView.text = deck.distinctBy { it.script }.joinToString("") { it.character }
            deckScriptsTextView.text = scripts.joinToString(" · ") { getString(it.labelRes) }
            deckCountTextView.text =
                resources.getQuantityString(R.plurals.deck_card_count, deck.size, deck.size)
        }
    }

    /** Makes the bear float gently up and down. */
    private fun startMascotAnimation() {
        if (!ValueAnimator.areAnimatorsEnabled()) return
        val offset = MASCOT_FLOAT_DP * resources.displayMetrics.density
        mascotAnimator = ObjectAnimator.ofFloat(binding.mascotImageView, View.TRANSLATION_Y, -offset, offset).apply {
            duration = MASCOT_FLOAT_DURATION_MS
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun navigate(@IdRes action: Int) {
        val navController = findNavController()
        // Ignores a second quick tap, which would crash because we already left this screen.
        if (navController.currentDestination?.id == R.id.mainFragment) {
            navController.navigate(action)
        }
    }

    private companion object {
        const val MASCOT_FLOAT_DP = 5f
        const val MASCOT_FLOAT_DURATION_MS = 1800L
    }
}
