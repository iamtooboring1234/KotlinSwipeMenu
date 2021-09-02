package my.edu.tarc.kotlinswipemenu

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.Adapter.myAdapter
import my.edu.tarc.kotlinswipemenu.Helper.MyButton
import my.edu.tarc.kotlinswipemenu.Helper.MySwipeHelper
import my.edu.tarc.kotlinswipemenu.Listener.MyButtonClickListener
import my.edu.tarc.kotlinswipemenu.Model.Insurance
import my.edu.tarc.kotlinswipemenu.Model.Item
import my.edu.tarc.kotlinswipemenu.databinding.FragmentListBinding


class ListFragment : Fragment() {

    lateinit var adapter : myAdapter
    lateinit var layoutManager: LinearLayoutManager

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Insurance")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)

        binding.recyclerTest.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        binding.recyclerTest.layoutManager = layoutManager

        val swipe = object: MySwipeHelper(requireActivity(), binding.recyclerTest, 200) {
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

        val insuranceList = ArrayList<Insurance>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for (insuranceSnapshot in snapshot.children){

                        insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)

                        println(insuranceList)

                    }


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        adapter = myAdapter(requireActivity(), insuranceList)
        binding.recyclerTest.adapter = adapter

        return binding.root
    }
}