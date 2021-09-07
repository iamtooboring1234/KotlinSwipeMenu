package my.edu.tarc.kotlinswipemenu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.viewModel.InsuranceApplication
import my.edu.tarc.kotlinswipemenu.R
import my.edu.tarc.kotlinswipemenu.databinding.InsuranceApplicationLayoutBinding
import my.edu.tarc.kotlinswipemenu.viewModel.Insurance
import java.text.SimpleDateFormat

private var tempInsList = ArrayList<Insurance>()
private val database = FirebaseDatabase.getInstance()
private val insuranceRef = database.getReference("Insurance")

class InsuranceApplicationAdapter (internal var insuranceApplicationList:MutableList<InsuranceApplication>, val clickListener: ViewListener) :
    RecyclerView.Adapter<InsuranceApplicationAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.insurance_application_layout, parent, false)
        return myViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = insuranceApplicationList[position]
        val format = SimpleDateFormat("dd-MMM-yyyy")

        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    tempInsList.clear()
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

                        tempInsList.add(insurance)

                    }

                    for(insList in tempInsList) {
                        if(insList.insuranceID.equals(currentItem.insuranceID)) {
                            holder.insuranceComp.text = insList.insuranceComp
                            holder.insuranceName.text = insList.insuranceName
                        }
                    }

                } else {
                    tempInsList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        holder.applicationID.text = currentItem.applicationID
        holder.applicationStatus.text = currentItem.insuranceStatus
        holder.insuranceID.text = currentItem.insuranceID
        holder.appliedDate.text = format.format(currentItem.insuranceAppliedDate)

        val isExpandable : Boolean = insuranceApplicationList[position].expandable
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.toExpandLayout.setOnClickListener() {
            val currentItem = insuranceApplicationList[position]
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position)
        }


        holder.bind(currentItem!!, clickListener)
    }

    override fun getItemCount(): Int {
        return insuranceApplicationList.size
    }

    class myViewHolder private constructor(val binding: InsuranceApplicationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InsuranceApplication, clickListener: ViewListener) {
            binding.insApp = item
            binding.executePendingBindings()
            binding.viewListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): myViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InsuranceApplicationLayoutBinding.inflate(layoutInflater, parent, false)
                return myViewHolder(binding)
            }
        }

        val applicationID: TextView = binding.tvInsuranceApplicationID
        val appliedDate: TextView = binding.tvAppliedDate
        val insuranceID: TextView = binding.tvApplicationInsuranceID
        val insuranceComp: TextView = binding.tvApplicationInsuranceComp
        val insuranceName: TextView = binding.tvApplicationInsuranceName
        val applicationStatus: TextView = binding.tvApplicationStatusText

        val expandableLayout: ConstraintLayout = binding.expandableLayout
        val toExpandLayout: ConstraintLayout = binding.toExpandLayout

    }

    class ViewListener(val clickListener: (ApplicationID: String, InsuranceID: String) -> Unit) {
        fun onClick(item: InsuranceApplication) =
            clickListener(item.applicationID!!, item.insuranceID!!)
    }

}
