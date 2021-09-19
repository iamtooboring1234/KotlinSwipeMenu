package my.edu.tarc.kotlinswipemenu

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.Helper.MyLottie
import my.edu.tarc.kotlinswipemenu.adapter.InsuranceApplicationAdapter
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListInsuranceApplicationBinding
import my.edu.tarc.kotlinswipemenu.viewModel.InsuranceApplication
import java.util.*
import kotlin.collections.ArrayList


class ListInsuranceApplicationFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val insuranceApplicationRef = database.getReference("InsuranceApplication")

    private lateinit var adapterInsApp : InsuranceApplicationAdapter

    private var insApplicationList = ArrayList<InsuranceApplication>()
    private var tempInsApplicationList = ArrayList<InsuranceApplication>()

    private lateinit var binding: FragmentListInsuranceApplicationBinding

    private var loadingDialog : Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListInsuranceApplicationBinding.inflate(inflater, container ,false)

        loadData()

        binding.rvInsApplication.setHasFixedSize(true)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ListInsuranceApplicationFragmentDirections.actionListInsuranceApplicationFragmentToNavigationFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackListInsuranceApplication.setOnClickListener() {
            val action = ListInsuranceApplicationFragmentDirections.actionListInsuranceApplicationFragmentToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        changeView(insApplicationList)

        binding.searchApplication.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    tempInsApplicationList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insApplicationList) {
                        val combineText = insurance.applicationID
                        if (combineText?.lowercase(Locale.getDefault())?.contains(search) == true) {
                            if(binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == insurance.applicationStatus) {
                                tempInsApplicationList.add(insurance)
                            } else if (binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == "All") {
                                tempInsApplicationList.add(insurance)
                            }
                        }
                    }
                    changeView(tempInsApplicationList)
                } else {
                    changeView(insApplicationList)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    tempInsApplicationList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insApplicationList) {
                        val combineText = insurance.applicationID
                        if (combineText?.lowercase(Locale.getDefault())?.contains(search) == true) {
                            if(binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == insurance.applicationStatus) {
                                tempInsApplicationList.add(insurance)
                            } else if (binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == "All") {
                                tempInsApplicationList.add(insurance)
                            }
                        }
                    }
                    changeView(tempInsApplicationList)
                } else {
                    changeView(insApplicationList)
                }
                return true
            }
        })

        binding.btnApplicationListFilter.setOnClickListener() {
            if(binding.filterLayout.visibility == View.GONE){
                binding.filterLayout.visibility = View.VISIBLE

                binding.applicationTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        var selectedApplication : ArrayList<InsuranceApplication> = ArrayList<InsuranceApplication>()
                        when (tab?.position) {
                            0 -> {
                                binding.searchApplication.setQuery("", false)
                                binding.searchApplication.clearFocus()
                                selectedApplication = insApplicationList
                            }
                            1 -> {
                                selectedApplication = insApplicationList.filter{ s -> s.applicationStatus == "Pending"} as ArrayList<InsuranceApplication>
                                selectedApplication = selectedApplication.filter{ s -> s.applicationID!!.contains(binding.searchApplication.query.toString())} as ArrayList<InsuranceApplication>
                            }
                            2 -> {
                                selectedApplication = insApplicationList.filter{ s -> s.applicationStatus == "Accepted"} as ArrayList<InsuranceApplication>
                                selectedApplication = selectedApplication.filter{ s -> s.applicationID!!.contains(binding.searchApplication.query.toString())} as ArrayList<InsuranceApplication>
                            }
                            3 -> {
                                selectedApplication = insApplicationList.filter{ s -> s.applicationStatus == "Rejected"} as ArrayList<InsuranceApplication>
                                selectedApplication = selectedApplication.filter{ s -> s.applicationID!!.contains(binding.searchApplication.query.toString())} as ArrayList<InsuranceApplication>
                            }
                        }

                        changeView(selectedApplication)

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        binding.searchApplication.setQuery("", false)
                        binding.searchApplication.clearFocus()
                    }

                })

            } else  {
                binding.filterLayout.visibility = View.GONE
            }

        }

        changeView(insApplicationList)

        return binding.root
    }

    private fun loadData() {
        insuranceApplicationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insApplicationList.clear()
                    for (insuranceSnapshot in snapshot.children) {

                        if (insuranceSnapshot.child("applicationStatus").value.toString() == "Pending" ||
                            insuranceSnapshot.child("applicationStatus").value.toString() == "Rejected" ||
                            insuranceSnapshot.child("applicationStatus").value.toString() == "Accepted"
                        ) {

                            val applicationID: String =
                                insuranceSnapshot.child("applicationID").value.toString()
                            val applicationAppliedDate: Date = Date(
                                insuranceSnapshot.child("applicationAppliedDate")
                                    .child("time").value as Long
                            )
                            val insuranceID: String =
                                insuranceSnapshot.child("insuranceID").value.toString()
                            val referralID: String =
                                insuranceSnapshot.child("referralID").value.toString()
                            val insuranceStatus: String =
                                insuranceSnapshot.child("applicationStatus").value.toString()

                            val insApp = InsuranceApplication(
                                applicationID,
                                insuranceID,
                                referralID,
                                applicationAppliedDate,
                                insuranceStatus,
                                false,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                false
                            )

                            insApplicationList.add(insApp)
                        }
                        binding.tvNoRecordFound.visibility = View.GONE
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.rvInsApplication.visibility = View.VISIBLE
                        binding.rvInsApplication.adapter?.notifyDataSetChanged()
                    }

                } else {
                    insApplicationList.clear()
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.tvNoRecordFound.visibility = View.VISIBLE
                    binding.rvInsApplication.visibility = View.INVISIBLE
                    binding.rvInsApplication.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun changeView(insuranceApplicationList: List<InsuranceApplication>) {
        adapterInsApp = InsuranceApplicationAdapter(insuranceApplicationList, InsuranceApplicationAdapter.ViewListener{
                applicationID,insuranceID -> val it = view

            showLoading()

            val action = ListInsuranceApplicationFragmentDirections.actionListInsuranceApplicationFragmentToUpdateInsuranceApplicationFragment(applicationID, insuranceID)
            view?.let { Navigation.findNavController(it).navigate(action) }

        })

        binding.rvInsApplication.adapter = adapterInsApp
        binding.rvInsApplication.adapter!!.notifyDataSetChanged()
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }


/*    private fun insertData() {

        val insAppList: List<InsuranceApplication> = listOf(
            InsuranceApplication("20210725-Etiqa-IA0001","IN001","IR001", Date("08/09/2021"),"Pending","bla"),
            InsuranceApplication("20210725-Etiqa-IA0002","IN001","IR001", Date("08/09/2021"),"Pending","bla")
        )

        for (insurance in insAppList) {
            insuranceApplicationRef.push().setValue(insurance)
        }

    }*/

}