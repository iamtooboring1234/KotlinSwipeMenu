package my.edu.tarc.kotlinswipemenu

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.Helper.MyLottie
import my.edu.tarc.kotlinswipemenu.adapter.InsuranceCustAdapter
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListInsuranceCustViewBinding
import my.edu.tarc.kotlinswipemenu.viewModel.Insurance
import java.util.*
import kotlin.collections.ArrayList

class ListInsuranceCustViewFragment : Fragment() {

    lateinit var insCustAdapter : InsuranceCustAdapter

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Insurance")

    private var insuranceList = ArrayList<Insurance>()
    private var tempinsuranceList = ArrayList<Insurance>()

    private lateinit var binding: FragmentListInsuranceCustViewBinding

    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListInsuranceCustViewBinding.inflate(inflater,  container ,false)

        binding.rvListInsuranceCust.setHasFixedSize(true)

        insCustAdapter = InsuranceCustAdapter(insuranceList, InsuranceCustAdapter.ApplyListener{
                insuranceID ->val it = view
            showLoading()
            Handler().postDelayed({
                hideLoading()
                val action = ListInsuranceCustViewFragmentDirections.actionListInsuranceCustViewFragmentToApplyInsuranceFragment(insuranceID)
                view?.let { Navigation.findNavController(it).navigate(action) }
            }, 3000)

        })

        binding.btnBackListInsuranceCustView.setOnClickListener() {
            val action = ListInsuranceCustViewFragmentDirections.actionListInsuranceCustViewFragmentToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.searchInsurance.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    tempinsuranceList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insuranceList) {
                        val combineText = insurance.insuranceName
                        if (combineText?.lowercase(Locale.getDefault())?.contains(search) == true) {
                            tempinsuranceList.add(insurance)
                        }
                    }

                    insCustAdapter = InsuranceCustAdapter(tempinsuranceList, InsuranceCustAdapter.ApplyListener{
                            insuranceID ->val it = view

                        val action = ListInsuranceCustViewFragmentDirections.actionListInsuranceCustViewFragmentToApplyInsuranceFragment(insuranceID)
                        view?.let { Navigation.findNavController(it).navigate(action) }

                    })

                    binding.rvListInsuranceCust.adapter = insCustAdapter
                    binding.rvListInsuranceCust.adapter!!.notifyDataSetChanged()

                }

                return true
            }

        })

        loadData()

        binding.rvListInsuranceCust.adapter = insCustAdapter

        return binding.root
    }

    private fun loadData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children){

                        val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                        val insuranceName: String = insuranceSnapshot.child("insuranceName").value.toString()
                        val insuranceComp: String = insuranceSnapshot.child("insuranceComp").value.toString()
                        val insurancePlan: String = insuranceSnapshot.child("insurancePlan").value.toString()
                        val insuranceType: String = insuranceSnapshot.child("insuranceType").value.toString()
                        var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                        for(child in insuranceSnapshot.child("insuranceCoverage").children){
                            insuranceCoverage.add(child.value.toString())
                        }

                        val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceType,insuranceCoverage)

                        insuranceList.add(insurance)

                    }

                    tempinsuranceList.addAll(insuranceList)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvListInsuranceCust.visibility = View.VISIBLE
                    binding.rvListInsuranceCust.adapter?.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

}