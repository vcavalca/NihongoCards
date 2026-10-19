package asuka.jyuku.nihongocards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asuka.jyuku.nihongocards.R
import asuka.jyuku.nihongocards.classes.applySystemBarsPadding
import asuka.jyuku.nihongocards.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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

        binding.startButton.setOnClickListener {
            navigate(R.id.action_mainFragment_to_gameFragment)
        }
        binding.settingsButton.setOnClickListener {
            navigate(R.id.action_mainFragment_to_settingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigate(@IdRes action: Int) {
        val navController = findNavController()
        // Ignores a second quick tap, which would crash because we already left this screen.
        if (navController.currentDestination?.id == R.id.mainFragment) {
            navController.navigate(action)
        }
    }
}
