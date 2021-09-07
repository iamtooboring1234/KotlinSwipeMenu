package my.edu.tarc.kotlinswipemenu

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.adapter.InsuranceAdapter
import my.edu.tarc.kotlinswipemenu.Helper.MyButton
import my.edu.tarc.kotlinswipemenu.Helper.MySwipeHelper
import my.edu.tarc.kotlinswipemenu.Listener.MyButtonClickListener
import my.edu.tarc.kotlinswipemenu.viewModel.Insurance
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListInsuranceBinding
import java.util.*
import kotlin.collections.ArrayList


class ListInsuranceFragment : Fragment() {

    lateinit var adapter : InsuranceAdapter

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Insurance")

    private var insuranceList = ArrayList<Insurance>()
    private var tempinsuranceList = ArrayList<Insurance>()

    private var tempbinding: FragmentListInsuranceBinding? = null
    private val binding get() = tempbinding!!

    private var strMsg:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempbinding = FragmentListInsuranceBinding.inflate(inflater,  container ,false)

        binding.rvInsuranceList.setHasFixedSize(true)

        val swipe = object: MySwipeHelper(requireActivity(), binding.rvInsuranceList, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(), "Delete", 30, R.drawable.ic_baseline_delete_white_24, Color.parseColor("#F30E15"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {

                            strMsg = "DELETE ID " + insuranceList[pos].insuranceID + " successfully deleted."

                            deleteData(insuranceList[pos].insuranceID, object : FirebaseSuccessListener {
                                override fun onDataFound(isDataFetched: Boolean, Key: String) {
                                    if (isDataFetched && Key.isNotEmpty()){
                                        myRef.child(Key).removeValue()
                                    }
                                }
                            })
                            Toast.makeText(requireContext(), strMsg, Toast.LENGTH_SHORT).show()
                        }
                    }))
                buffer.add(MyButton(requireContext(), "Update", 30, R.drawable.ic_baseline_edit_24, Color.parseColor("#2266FF"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {

                            val amount = insuranceList[pos].insuranceID

                            val action = ListInsuranceFragmentDirections.actionListInsuranceFragmentToUpdateInsuranceFragment(amount)
                            view?.let { Navigation.findNavController(it).navigate(action) }
                        }
                    }))
                }
            }

        binding.btnAdd.setOnClickListener(){
            val action = ListInsuranceFragmentDirections.actionListInsuranceFragmentToInsuranceAddFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnBackListInsurance.setOnClickListener() {
            val action = ListInsuranceFragmentDirections.actionListInsuranceFragmentToNavigationFragment()
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
                        val combineText:String = insurance.insuranceID + "-" + insurance.insuranceComp + "-" + insurance.insuranceName
                        if (combineText.lowercase(Locale.getDefault()).contains(search)) {
                            tempinsuranceList.add(insurance)
                        }
                    }

                    adapter = InsuranceAdapter(requireActivity(), tempinsuranceList)
                    binding.rvInsuranceList.adapter = adapter

                    binding.rvInsuranceList.adapter!!.notifyDataSetChanged()

                }

                return true
            }

        })

        loadData()
        onRefresh()

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
                    binding.rvInsuranceList.visibility = View.VISIBLE
                    binding.rvInsuranceList.adapter?.notifyDataSetChanged()

                } else {
                    insuranceList.clear()
                    tempinsuranceList.clear()
                    binding.rvInsuranceList.visibility = View.INVISIBLE
                    binding.rvInsuranceList.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        adapter = InsuranceAdapter(requireActivity(), insuranceList)
        binding.rvInsuranceList.adapter = adapter
    }

    private fun deleteData(insuranceID : String?, dataFetched:FirebaseSuccessListener) {

        myRef.orderByChild("insuranceID").equalTo(insuranceID).addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    if (ds.exists()){
                        ds.key?.let { dataFetched.onDataFound(true, it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun onRefresh() {
        binding.srlInsuranceList.setOnRefreshListener {
            loadData()
            Toast.makeText(requireContext(), "Refresh", Toast.LENGTH_SHORT).show()
            binding.srlInsuranceList.isRefreshing = false
        }
    }

    //Data Initialize
    private fun insertData(){
        val insList: List<Insurance> = listOf(
            Insurance("IN001","Car insurance","Etiqa","Plan A", "Act Cover",listOf("Coverage 1")),
            Insurance("IN002","Motor insurance","Prudential","Plan C", "Third Party Cover",listOf("Coverage 1")),
            Insurance("IN003","Truck insurance","Etiqa","Plan D", "Third Party, Fire and Theft cover",listOf("Coverage 1")),
            Insurance("IN005","Truck insurance","AIA","Plan D", "Third Party, Fire and Theft cover",listOf("Coverage 1")),
            Insurance("IN006","Truck insurance","Great Eastern","Plan D", "Comprehensive cover",listOf("Coverage 1")),
            Insurance("IN004","Van insurance","Prudential","Plan B", "Act Cover",listOf("Coverage 1") )
        )

        for (insurance in insList) {
            myRef.push().setValue(insurance)
        }

    }

    interface FirebaseSuccessListener {
        fun onDataFound(isDataFetched: Boolean, Key: String)
    }

}