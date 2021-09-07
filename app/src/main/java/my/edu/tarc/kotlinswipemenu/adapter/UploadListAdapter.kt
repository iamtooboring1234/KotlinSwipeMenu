package my.edu.tarc.kotlinswipemenu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.kotlinswipemenu.R
import my.edu.tarc.kotlinswipemenu.databinding.EvidencesItemLayoutBinding
import my.edu.tarc.kotlinswipemenu.viewModel.File

class UploadListAdapter(internal var fileNameList: MutableList<File>, val clickListener: RemoveListener) :
    RecyclerView.Adapter<UploadListAdapter.myViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.evidences_item_layout, parent, false)
        return myViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return fileNameList.size
    }

    class myViewHolder private constructor(val binding: EvidencesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: File, clickListener: UploadListAdapter.RemoveListener) {
            binding.file = item
            binding.executePendingBindings()
            binding.removeListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): myViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EvidencesItemLayoutBinding.inflate(layoutInflater, parent, false)
                return myViewHolder(binding)
            }
        }

        val fileName: TextView = binding.tvFileName

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = fileNameList[position]
        holder.fileName.text = currentItem.FileName
        holder.bind(currentItem!!, clickListener)
    }

    class RemoveListener(val clickListener: (file: File) -> Unit) {
        fun onClick(item: File) =
            clickListener(item)
    }

}
