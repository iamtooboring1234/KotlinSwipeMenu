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
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.kotlinswipemenu.databinding.FragmentApplyInsuranceBinding
import my.edu.tarc.kotlinswipemenu.viewModel.Insurance


class ApplyInsuranceFragment : Fragment() {

    private var tempbinding: FragmentApplyInsuranceBinding? = null
    private val binding get() = tempbinding!!
    private var fileNameList = ArrayList<String>()

    private var mStorange : StorageReference = FirebaseStorage.getInstance().getReference()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempbinding = FragmentApplyInsuranceBinding.inflate(inflater,  container ,false)

        binding.btnUpload.setOnClickListener() {

            val filesIntent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            filesIntent.addCategory(Intent.CATEGORY_OPENABLE)
            filesIntent.type = "*/*" //use image/* for photos, etc.

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

                    println(fileUri)
                    println(fileName)

                    fileNameList.add(fileName)
                    binding.textView2.text = fileNameList.toString()

                    var fileToUplaod :StorageReference = mStorange.child("Images").child(fileName)

                    fileToUplaod.putFile(fileUri).addOnSuccessListener {
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (data?.data != null){

                Toast.makeText(context, "Selected single", Toast.LENGTH_SHORT).show()


                    val fileUri: Uri = data.data!!

                    val fileName: String = getFileName(fileUri)

                    println(fileUri)
                    println(fileName)

                    fileNameList.add(fileName)
                    binding.textView2.text = fileNameList.toString()

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