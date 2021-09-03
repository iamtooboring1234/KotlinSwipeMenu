package my.edu.tarc.kotlinswipemenu

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.Adapter.myAdapter
import my.edu.tarc.kotlinswipemenu.Helper.MyButton
import my.edu.tarc.kotlinswipemenu.Helper.MySwipeHelper
import my.edu.tarc.kotlinswipemenu.Listener.MyButtonClickListener
import my.edu.tarc.kotlinswipemenu.Model.Insurance
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListBinding
import java.util.*
import kotlin.collections.ArrayList


class ListFragment : Fragment() {

    lateinit var adapter : myAdapter
    lateinit var layoutManager: LinearLayoutManager

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Insurance")

    private var insuranceList = ArrayList<Insurance>()
    private var tempinsuranceList = ArrayList<Insurance>()

    private var tempbinding: FragmentListBinding? = null
    private val binding get() = tempbinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        tempbinding = FragmentListBinding.inflate(inflater,  container ,false)

        binding.rvInsuranceList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        binding.rvInsuranceList.layoutManager = layoutManager

        val swipe = object: MySwipeHelper(requireActivity(), binding.rvInsuranceList, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(), "Delete", 30, R.drawable.ic_baseline_delete_24, Color.parseColor("#FFFFFF"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {
                            Toast.makeText(requireContext(), "DELETE ID$pos", Toast.LENGTH_SHORT).show()
                        }
                    }))
                buffer.add(MyButton(requireContext(), "Update", 30, R.drawable.ic_baseline_edit_24, Color.parseColor("#6F6F6F"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {
                            Toast.makeText(requireContext(), "UPDATE ID$pos", Toast.LENGTH_SHORT).show()
                        }
                    }))
            }
        }

        loadData()

        binding.searchInsurance.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    tempinsuranceList.clear()
                    val search = newText.toLowerCase(Locale.getDefault())
                    for (insurance in insuranceList) {
                        if (insurance.insuranceName?.toLowerCase(Locale.getDefault())?.contains(search) == true) {
                            tempinsuranceList.add(insurance)
                        }
                    }

                    adapter = myAdapter(requireActivity(), tempinsuranceList)
                    binding.rvInsuranceList.adapter = adapter

                    binding.rvInsuranceList.adapter!!.notifyDataSetChanged()

                } else {
                    tempinsuranceList.clear()
                    tempinsuranceList.addAll(insuranceList)

                    adapter = myAdapter(requireActivity(), insuranceList)
                    binding.rvInsuranceList.adapter = adapter

                    binding.rvInsuranceList.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })

        return binding.root
    }

    private fun loadData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (insuranceSnapshot in snapshot.children){
                        insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)
                    }
                    tempinsuranceList.addAll(insuranceList)
                    binding.rvInsuranceList.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        adapter = myAdapter(requireActivity(), insuranceList)
        binding.rvInsuranceList.adapter = adapter
    }
}