package my.edu.tarc.kotlinswipemenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.kotlinswipemenu.Model.Insurance
import my.edu.tarc.kotlinswipemenu.Model.InsuranceApplication
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListInsuranceApplicationBinding

class ListInsuranceApplicationFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("InsuranceApplication")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentListInsuranceApplicationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_insurance_application, container, false)

        insertData()

        return binding.root
    }


    private fun insertData() {

        val insAppList: List<InsuranceApplication> = listOf(
            InsuranceApplication("20210725-Etiqa-IA0001","IN001","IR001", "bla","Pending","bla"),
            InsuranceApplication("20210725-Etiqa-IA0001","IN001","IR001", "bla","Pending","bla")
        )

        for (insurance in insAppList) {
            myRef.push().setValue(insurance)
        }

    }

}