package my.edu.tarc.kotlinswipemenu

import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.kotlinswipemenu.Helper.MyLottie
import my.edu.tarc.kotlinswipemenu.adapter.GetEvidenceAdapter
import my.edu.tarc.kotlinswipemenu.databinding.FragmentUpdateInsuranceApplicationBinding
import my.edu.tarc.kotlinswipemenu.viewModel.File

class UpdateInsuranceApplicationFragment : Fragment() {

    private lateinit var binding: FragmentUpdateInsuranceApplicationBinding
    private var fileNameList = ArrayList<File>()
    private lateinit var fileAdapter : GetEvidenceAdapter
    private lateinit var fileToUpload : StorageReference

    private var mStorage : StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var referralUID: String

    private val args: UpdateInsuranceApplicationFragmentArgs by navArgs()

    private lateinit var progressDownloadDialog : ProgressDialog
    private var completeDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_insurance_application, container, false)

        progressDownloadDialog = ProgressDialog(this.context)
        progressDownloadDialog.setMessage("Downloading the file..")
        progressDownloadDialog.setCancelable(false)
        progressDownloadDialog.setCanceledOnTouchOutside(false)

        referralUID = "LQx5dlt2D1ZX1P6fCEXcSP4NEN83"
        val storageFile = FirebaseStorage.getInstance().getReference("Evidences Insurance Application").child("User_$referralUID").child(args.applicationID.toString())
        storageFile.listAll().addOnSuccessListener { it ->
            for(item in it.items){
                item.downloadUrl.addOnSuccessListener {
                    val uri = it
                    val name = getFileName(uri!!)
                    val file = File(FileName = name, FileUri = uri)
                    fileNameList.add(file)
                    bindFiles()
                }
            }
        }

        return binding.root
    }

    private fun bindFiles(){
        fileAdapter = GetEvidenceAdapter(fileNameList, GetEvidenceAdapter.DownloadListener {
                File ->val it = view
            var position:Int = 0

            try {
                for (i in 0 until fileNameList.size) {
                    if (File.FileName.equals(fileNameList[i].FileName)) {
                        Toast.makeText(context, "File ${File.FileName} is downloading...", Toast.LENGTH_SHORT).show()
                        downloadFile(fileNameList[i])
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.rvEvidenceListGetEvidence.setHasFixedSize(true)
        binding.rvEvidenceListGetEvidence.adapter = fileAdapter
        fileAdapter.notifyDataSetChanged()
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

    private fun  downloadFile(file: File) {
        showLoading()
        progressDownloadDialog.show()

        var total: Long = 0

        val storageFile = FirebaseStorage.getInstance().getReference("Evidences Insurance Application").child("User_$referralUID").child(args.applicationID.toString())
            .child(file.FileName.toString())

        storageFile.metadata.addOnSuccessListener  {

            total = it.sizeBytes

            Handler().postDelayed({
                hideLoading()
            }, (total/100)*2)

            val handler = Handler()

            val progressRunnable = Runnable {
                val request = DownloadManager.Request(file.FileUri)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Visibility of the download Notification
                    .setTitle(file.FileName) // Title of the Download Notification
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file.FileName)
                    .setDescription("From ") // Description of the Download Notification
                    .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true) // Set if download is allowed on roaming network

                val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                val downloadID = downloadManager!!.enqueue(request) // enqueue puts the download request in the queue.
                progressDownloadDialog.dismiss()
                Toast.makeText(context, "Download Complete.", Toast.LENGTH_SHORT).show()
            }

            handler.postDelayed(progressRunnable, total/100)

        }

    }

    private fun hideLoading() {
        completeDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        completeDialog = MyLottie.showCompleteDialog(requireContext())
    }

}


