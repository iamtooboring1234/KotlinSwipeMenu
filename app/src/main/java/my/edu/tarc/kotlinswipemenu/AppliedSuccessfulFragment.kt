package my.edu.tarc.kotlinswipemenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import my.edu.tarc.kotlinswipemenu.databinding.FragmentAppliedSuccessfulBinding
import my.edu.tarc.kotlinswipemenu.databinding.FragmentApplyInsuranceBinding

class AppliedSuccessfulFragment : Fragment() {

    private lateinit var binding: FragmentAppliedSuccessfulBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppliedSuccessfulBinding.inflate(inflater,  container ,false)

        binding.btnBackToMenu.setOnClickListener() {
            val action = AppliedSuccessfulFragmentDirections.actionAppliedSuccessfulFragmentToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

}