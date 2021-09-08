package my.edu.tarc.kotlinswipemenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.kotlinswipemenu.databinding.FragmentNavigationBinding

class NavigationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentNavigationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_navigation, container, false)

        binding.btnToApplication.setOnClickListener() {
            val action = NavigationFragmentDirections.actionNavigationFragmentToListInsuranceApplicationFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnToApply.setOnClickListener() {
            val action = NavigationFragmentDirections.actionNavigationFragmentToListInsuranceCustViewFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnToList.setOnClickListener() {
            val action = NavigationFragmentDirections.actionNavigationFragmentToListInsuranceFragment()
            Navigation.findNavController(it).navigate(action)
        }




        return binding.root
    }

}