package my.edu.tarc.kotlinswipemenu

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.kotlinswipemenu.adapter.UploadListAdapter
import my.edu.tarc.kotlinswipemenu.databinding.FragmentApplyInsuranceBinding
import my.edu.tarc.kotlinswipemenu.viewModel.File

class ApplyInsuranceFragment : Fragment() {

    private var tempbinding: FragmentApplyInsuranceBinding? = null
    private val binding get() = tempbinding!!
    private var fileNameList = ArrayList<File>()
    private lateinit var fileAdapter : UploadListAdapter
    private lateinit var fileToUplaod :StorageReference

    private var mStorange : StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempbinding = FragmentApplyInsuranceBinding.inflate(inflater,  container ,false)

        fileAdapter = UploadListAdapter(fileNameList, UploadListAdapter.RemoveListener {
                File ->val it = view
            var position:Int = 0

            println(fileNameList.size)

            try {
                for (i in 0 until fileNameList.size) {
                    if (File.FileName.equals(fileNameList[i].FileName)) {
                        fileNameList.removeAt(i)
                        binding.rvFileUpload.adapter = fileAdapter
                        fileAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Delete too fast, slow down.", Toast.LENGTH_SHORT).show()
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

            Toast.makeText(context, "Upload", Toast.LENGTH_SHORT).show()
        }

        return binding.root

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777 && resultCode == RESULT_OK) {

            if(data?.clipData != null) {
                Toast.makeText(context, "Selected multiple", Toast.LENGTH_SHORT).show()

                var totalItemSelected:Int = data.clipData!!.itemCount

                println(totalItemSelected)

                for (count in 0 until totalItemSelected) {

                    val fileUri: Uri = data.clipData!!.getItemAt(count).uri

                    val fileName: String = getFileName(fileUri)

                    val selectedFile = File(fileName, fileUri)

                    fileNameList.add(selectedFile)

                    fileAdapter.notifyDataSetChanged()

                    binding.btnApply.setOnClickListener() {
                        for(fileCount in 0 until fileNameList.size) {
                            fileToUplaod = fileNameList[fileCount].FileName?.let { it ->
                                mStorange.child("Evidences Insurance Application").child(
                                    it
                                )
                            }!!

                            fileNameList[fileCount].FileUri?.let { it ->
                                fileToUplaod.putFile(it).addOnSuccessListener {
                                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                }

            } else if (data?.data != null){

                Toast.makeText(context, "Selected single", Toast.LENGTH_SHORT).show()


                    val fileUri: Uri = data.data!!

                    val fileName: String = getFileName(fileUri)

                    println(fileUri)
                    println(fileName)


            }
        }
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


}