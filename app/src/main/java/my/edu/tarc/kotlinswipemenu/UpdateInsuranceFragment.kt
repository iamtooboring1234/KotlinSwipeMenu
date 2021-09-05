package my.edu.tarc.kotlinswipemenu

import android.R
import android.os.Bundle
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.kotlinswipemenu.Adapter.InsuranceAdapter
import my.edu.tarc.kotlinswipemenu.Model.Insurance
import my.edu.tarc.kotlinswipemenu.databinding.FragmentUpdateInsuranceBinding

class UpdateInsuranceFragment : Fragment() {

    lateinit var adapter : InsuranceAdapter

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")

    private var tempbinding: FragmentUpdateInsuranceBinding? = null
    private val binding get() = tempbinding!!

    private var insuranceList = ArrayList<Insurance>()
    private var insuranceTypeList = ArrayList<String>()

    private var insuranceID: String? = ""
    private val args: UpdateInsuranceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        tempbinding = FragmentUpdateInsuranceBinding.inflate(inflater,  container ,false)

        binding.btnBackUpdateInsurance.setOnClickListener() {
            val action = UpdateInsuranceFragmentDirections.actionUpdateInsuranceFragmentToListInsuranceFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnResetUpdateInsurance.setOnClickListener() {
            resetForm()
        }

        binding.btnUpdateUpdateInsurance.setOnClickListener(){
            updateData()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadData(args.insuranceID.toString())
    }

    private fun loadData(insuranceID : String) {
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

                    val spinner: Spinner = binding.ddlUpdateInsuranceType

                    val adapterIns: ArrayAdapter<String>? =
                        context?.let { ArrayAdapter<String>(it.getApplicationContext(), R.layout.simple_spinner_item, insuranceTypeList) }
                    adapterIns?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinner.adapter = adapterIns
                    adapterIns?.notifyDataSetChanged()

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }

                } else {
                    insuranceList.clear()
                    insuranceTypeList.clear()
                }

                for(ds in insuranceList) {
                    if(ds.insuranceID.equals(insuranceID)) {
                        binding.tfUpdateInsuranceName.setText(ds.insuranceName)
                        binding.tfUpdateInsuranceComp.setText(ds.insuranceComp)
                        binding.tfUpdateInsurancePlan.setText(ds.insurancePlan)
                        if(ds.insuranceCoverage?.contains(binding.cbBodyInjury.text.toString()) == true) {
                            if(!binding.cbBodyInjury.isChecked) {
                                binding.cbBodyInjury.toggle()
                            }
                        } else {
                            if (binding.cbBodyInjury.isChecked) {
                                binding.cbBodyInjury.toggle()
                            }
                        }
                        if(ds.insuranceCoverage?.contains(binding.cbInsured.text.toString()) == true) {
                            if(!binding.cbInsured.isChecked) {
                                binding.cbInsured.toggle()
                            }
                        } else {
                            if (binding.cbInsured.isChecked) {
                                binding.cbInsured.toggle()
                            }
                        }
                        if(ds.insuranceCoverage?.contains(binding.cbMedical.text.toString()) == true) {
                            if (!binding.cbMedical.isChecked) {
                                binding.cbMedical.toggle()
                           }
                        } else {
                            if (binding.cbMedical.isChecked) {
                                binding.cbMedical.toggle()
                            }
                        }
                        if(ds.insuranceCoverage?.contains(binding.cbPropertyDmg.text.toString()) == true) {
                            if(!binding.cbPropertyDmg.isChecked) {
                                binding.cbPropertyDmg.toggle()
                            }
                        } else {
                            if (binding.cbPropertyDmg.isChecked) {
                                binding.cbPropertyDmg.toggle()
                            }
                        }
                        ds.insuranceType?.let {
                            selectSpinnerItemByValue(binding.ddlUpdateInsuranceType,
                                it
                            )
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun selectSpinnerItemByValue(spnr: Spinner, value: String) {
        val adapter = spnr.adapter
        for (position in 0 until adapter.count) {
            if (adapter.getItem(position).toString() == value) {
                spnr.setSelection(position)
                return
            }
        }
    }

    private fun resetForm() {
        loadData(args.insuranceID.toString())
    }

    private fun updateData() {

        val insuranceComp:String = binding.tfUpdateInsuranceComp.text.toString()
        val insuranceName:String = binding.tfUpdateInsuranceName.text.toString()
        val insurancePlan:String = binding.tfUpdateInsurancePlan.text.toString()
        val insuranceType:String = binding.ddlUpdateInsuranceType.selectedItem.toString()
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

        val insurance = mapOf<String, Any?>(
            "insuranceName" to  insuranceName,
            "insurancePlan" to insurancePlan,
            "insuranceCoverage" to insuranceCoverage,
            "insuranceComp" to insuranceComp,
            "insuranceType" to insuranceType
        )

        insuranceRef.orderByChild("insuranceID").equalTo(args.insuranceID.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    if (ds.exists()){
                        ds.key?.let {
                            insuranceRef.child(it).updateChildren(insurance)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Update Successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    loadData(args.insuranceID.toString())
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }

}