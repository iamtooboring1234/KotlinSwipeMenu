package my.edu.tarc.kotlinswipemenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.*
import my.edu.tarc.kotlinswipemenu.adapter.InsuranceApplicationAdapter
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListInsuranceApplicationBinding
import my.edu.tarc.kotlinswipemenu.viewModel.InsuranceApplication
import java.util.*
import kotlin.collections.ArrayList


class ListInsuranceApplicationFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val insuranceApplicationRef = database.getReference("InsuranceApplication")

    lateinit var adapterInsApp : InsuranceApplicationAdapter

    private var insApplicationList = ArrayList<InsuranceApplication>()
    private var tempInsApplicationList = ArrayList<InsuranceApplication>()

    private var tempbinding: FragmentListInsuranceApplicationBinding? = null
    private val binding get() = tempbinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempbinding = FragmentListInsuranceApplicationBinding.inflate(inflater, container ,false)

        loadData()

        binding.rvInsApplication.setHasFixedSize(true)

        binding.btnBackListInsuranceApplication.setOnClickListener() {
            val action = ListInsuranceApplicationFragmentDirections.actionListInsuranceApplicationFragmentToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        adapterInsApp = InsuranceApplicationAdapter(insApplicationList, InsuranceApplicationAdapter.ViewListener{
                applicationID,insuranceID ->val it = view

            Toast.makeText(context, "hi", Toast.LENGTH_LONG).show()

        })

        binding.searchApplication.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    tempInsApplicationList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insApplicationList) {
                        val combineText = insurance.applicationID
                        if (combineText?.lowercase(Locale.getDefault())?.contains(search) == true) {
                            tempInsApplicationList.add(insurance)
                        }
                    }

                    adapterInsApp = InsuranceApplicationAdapter(tempInsApplicationList, InsuranceApplicationAdapter.ViewListener{
                            applicationID,insuranceID ->val it = view

                        Toast.makeText(context, "hi", Toast.LENGTH_LONG).show()

                    })
                    binding.rvInsApplication.adapter = adapterInsApp

                    binding.rvInsApplication.adapter!!.notifyDataSetChanged()

                } else {
                    tempInsApplicationList.clear()
                    tempInsApplicationList.addAll(insApplicationList)

                    binding.rvInsApplication.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })

        binding.rvInsApplication.adapter = adapterInsApp

        return binding.root
    }

    private fun loadData() {
        insuranceApplicationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insApplicationList.clear()
                    for (insuranceSnapshot in snapshot.children) {

                        val insuranceApplicationID: String =
                            insuranceSnapshot.child("applicationID").value.toString()
                        val evidences: String =
                            insuranceSnapshot.child("evidences").value.toString()
                        val insuranceAppliedDate: Date = Date(
                            insuranceSnapshot.child("insuranceAppliedDate")
                                .child("time").value as Long
                        )
                        val insuranceID: String =
                            insuranceSnapshot.child("insuranceID").value.toString()
                        val insuranceReferralID: String =
                            insuranceSnapshot.child("insuranceReferralID").value.toString()
                        val insuranceStatus: String =
                            insuranceSnapshot.child("insuranceStatus").value.toString()

                        val insApp = InsuranceApplication(
                            insuranceApplicationID,
                            insuranceID,
                            insuranceReferralID,
                            insuranceAppliedDate,
                            insuranceStatus,
                            evidences
                        )

                        insApplicationList.add(insApp)
                    }

                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvInsApplication.visibility = View.VISIBLE
                    binding.rvInsApplication.adapter?.notifyDataSetChanged()

                } else {
                    insApplicationList.clear()
                    binding.rvInsApplication.visibility = View.INVISIBLE
                    binding.rvInsApplication.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun insertData() {

        val insAppList: List<InsuranceApplication> = listOf(
            InsuranceApplication("20210725-Etiqa-IA0001","IN001","IR001", Date("08/09/2021"),"Pending","bla"),
            InsuranceApplication("20210725-Etiqa-IA0002","IN001","IR001", Date("08/09/2021"),"Pending","bla")
        )

        for (insurance in insAppList) {
            insuranceApplicationRef.push().setValue(insurance)
        }

    }

}