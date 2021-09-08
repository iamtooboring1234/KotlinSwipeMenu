package my.edu.tarc.kotlinswipemenu

import android.R
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.kotlinswipemenu.adapter.UploadListAdapter
import my.edu.tarc.kotlinswipemenu.databinding.FragmentApplyInsuranceBinding
import my.edu.tarc.kotlinswipemenu.functions.checkUser
import my.edu.tarc.kotlinswipemenu.viewModel.File
import my.edu.tarc.kotlinswipemenu.viewModel.Insurance

class ApplyInsuranceFragment : Fragment() {

    private var insuranceCustList = ArrayList<Insurance>()

    private var tempbinding: FragmentApplyInsuranceBinding? = null
    private val binding get() = tempbinding!!

    private var fileNameList = ArrayList<File>()

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")
    private val insuranceApplicationRef = database.getReference("InsuranceApplication")

    private lateinit var fileAdapter : UploadListAdapter
    private lateinit var fileToUplaod :StorageReference
    private var mStorange : StorageReference = FirebaseStorage.getInstance().reference

    private val args: ApplyInsuranceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempbinding = FragmentApplyInsuranceBinding.inflate(inflater,  container ,false)

        fileAdapter = UploadListAdapter(fileNameList, UploadListAdapter.RemoveListener {
                File ->val it = view

            try {
                for (i in 0 until fileNameList.size) {
                    if (File.FileName.equals(fileNameList[i].FileName)) {
                        Toast.makeText(context, "File deleted.", Toast.LENGTH_SHORT).show()
                        fileNameList.removeAt(i)
                        binding.rvFileUpload.adapter = fileAdapter
                        fileAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {

            }
        })

        binding.rvFileUpload.setHasFixedSize(true)
        binding.rvFileUpload.adapter = fileAdapter
        fileAdapter.notifyDataSetChanged()

        binding.btnUpload.setOnClickListener() {

            val filesIntent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            filesIntent.addCategory(Intent.CATEGORY_OPENABLE)
            filesIntent.type = "*/*"

            startActivityForResult(Intent.createChooser(filesIntent, "Select PDF File"), 777)

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadData(args.insuranceID.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777 && resultCode == RESULT_OK) {

            if(data?.clipData != null) {

                var totalItemSelected:Int = data.clipData!!.itemCount


                for (count in 0 until totalItemSelected) {

                    val fileUri: Uri = data.clipData!!.getItemAt(count).uri

                    val fileName: String = getFileName(fileUri)

                    val selectedFile = File(fileName, fileUri)

                    fileNameList.add(selectedFile)

                    fileAdapter.notifyDataSetChanged()

                    binding.btnApply.setOnClickListener() {
                        for(fileCount in 0 until fileNameList.size) {
                            fileToUplaod = fileNameList[fileCount].FileName?.let { it ->
                                checkUser().getCurrentUserUID()?.let { it1 ->
                                    mStorange.child("Evidences Insurance Application").child(it1).child(
                                        it
                                    )
                                }
                            }!!

                            fileNameList[fileCount].FileUri?.let { it ->
                                fileToUplaod.putFile(it).addOnSuccessListener {
                                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        val


                        addInsuranceApplication()
                    }

                }

            } else if (data?.data != null){

                val fileUri: Uri = data.data!!

                val fileName: String = getFileName(fileUri)

                val selectedFile = File(fileName, fileUri)

                fileNameList.add(selectedFile)

                fileAdapter.notifyDataSetChanged()

                binding.btnApply.setOnClickListener() {
                    for(fileCount in 0 until fileNameList.size) {
                        fileToUplaod = fileNameList[fileCount].FileName?.let { it ->
                            checkUser().getCurrentUserUID()?.let { it1 ->
                                mStorange.child("Evidences Insurance Application").child("User_$it1").child(
                                    it
                                )
                            }
                        }!!

                        fileNameList[fileCount].FileUri?.let { it ->
                            fileToUplaod.putFile(it).addOnSuccessListener {
                                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addInsuranceApplication() {
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

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun loadData(insuranceID : String) {
        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insuranceCustList.clear()
                    for (insuranceSnapshot in snapshot.children){
                        if(insuranceSnapshot.child("insuranceID").value.toString() == insuranceID) {
                            val insuranceID: String =
                                insuranceSnapshot.child("insuranceID").value.toString()
                            val insuranceName: String =
                                insuranceSnapshot.child("insuranceName").value.toString()
                            val insuranceComp: String =
                                insuranceSnapshot.child("insuranceComp").value.toString()
                            val insurancePlan: String =
                                insuranceSnapshot.child("insurancePlan").value.toString()
                            val insuranceType: String =
                                insuranceSnapshot.child("insuranceType").value.toString()
                            var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                            for (child in insuranceSnapshot.child("insuranceCoverage").children) {
                                insuranceCoverage.add(child.value.toString())
                            }

                            val insurance = Insurance(
                                insuranceID,
                                insuranceName,
                                insuranceComp,
                                insurancePlan,
                                insuranceType,
                                insuranceCoverage
                            )

                            insuranceCustList.add(insurance)
                        }
                    }

                    for (insCustList in insuranceCustList) {
                        binding.tvCustInsuranceName.setText(insCustList.insuranceName)
                        binding.tvCustInsuranceComp.setText(insCustList.insuranceComp)
                        binding.tvCustInsurancePlan.setText(insCustList.insurancePlan)
                        binding.tvCustInsuranceType.setText(insCustList.insuranceType)

                        var strCover : String? = ""
                        for (insCover in insCustList.insuranceCoverage!!) {
                            strCover += "$insCover,\n"
                        }

                        binding.tvCustInsuranceCoverage.setText(strCover)
                    }

                } else {
                    insuranceCustList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}