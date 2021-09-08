package my.edu.tarc.kotlinswipemenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.adapter.InsuranceAdapter
import my.edu.tarc.kotlinswipemenu.viewModel.Insurance
import my.edu.tarc.kotlinswipemenu.databinding.FragmentAddInsuranceBinding
import my.edu.tarc.kotlinswipemenu.functions.ResetForm


class InsuranceAddFragment : Fragment() {

    lateinit var adapter : InsuranceAdapter

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")

    private lateinit var binding: FragmentAddInsuranceBinding

    private var insuranceList = ArrayList<Insurance>()
    private var insuranceTypeList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadData()

        binding = FragmentAddInsuranceBinding.inflate(inflater,  container ,false)

        binding.btnBackAddInsurance.setOnClickListener() {
            val action = InsuranceAddFragmentDirections.actionInsuranceAddFragmentToListInsuranceFragment()
            Navigation.findNavController(it).navigate(action)   
        }

        binding.btnResetAddInsurance.setOnClickListener(){
            ResetForm().resetAllField(view as ViewGroup)
        }

        binding.btnAddAddInsurance.setOnClickListener() {

            val insuranceComp:String = binding.tfAddInsuranceComp.text.toString()
            val insuranceName:String = binding.tfAddInsuranceName.text.toString()
            val insurancePlan:String = binding.tfAddInsurancePlan.text.toString()
            val insuranceType:String = binding.ddlAddInsuranceType.selectedItem.toString()
            var insuranceCoverage: ArrayList<String> = ArrayList()

            if(binding.cbBodyInjury.isChecked){
                insuranceCoverage.add(binding.cbBodyInjury.text.toString())
            }
            if(binding.cbInsured.isChecked){
                insuranceCoverage.add(binding.cbInsured.text.toString())
            }
            if(binding.cbMedical.isChecked){
                insuranceCoverage.add(binding.cbMedical.text.toString())
            }
            if(binding.cbPropertyDmg.isChecked){
                insuranceCoverage.add(binding.cbPropertyDmg.text.toString())
            }

            if(insuranceComp.isNotEmpty() && insuranceName.isNotEmpty() && insurancePlan.isNotEmpty() && insuranceType.isNotEmpty() && insuranceCoverage.isNotEmpty()) {
                insertData(insuranceComp,insuranceName,insurancePlan,insuranceType,insuranceCoverage)
            } else {
                Toast.makeText(requireContext(), "Please fill in the required details!", Toast.LENGTH_LONG).show()
            }

        }

        return binding.root
    }

    private fun insertData(insuranceComp: String, insuranceName: String, insurancePlan: String, insuranceType: String, insuranceCoverage: ArrayList<String>) {

        var newID:String = ""

        insuranceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newID = "IN" + "%03d".format(snapshot.childrenCount + 1)
                } else {
                    newID = "RR0001"
                }

                val newInsurance = Insurance(newID, insuranceName, insuranceComp, insurancePlan, insuranceType, insuranceCoverage)

                insuranceRef.push().setValue(newInsurance).addOnSuccessListener(){
                    Toast.makeText(context, "Add successful", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Add unsuccessful", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun loadData() {
        insuranceRef.addValueEventListener(object : ValueEventListener {
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

                    for(ds in insuranceList) {
                        if(!insuranceTypeList.contains(ds.insuranceType)) {
                            ds.insuranceType?.let { insuranceTypeList.add(it) }
                        }
                    }

                    val spinner: Spinner = binding.ddlAddInsuranceType

                    val adapterIns: ArrayAdapter<String> =
                        ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, insuranceTypeList)
                    adapterIns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinner.adapter = adapterIns
                    adapterIns.notifyDataSetChanged()

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val text = parent?.selectedItem.toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }

                } else {
                    insuranceList.clear()
                    insuranceTypeList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}