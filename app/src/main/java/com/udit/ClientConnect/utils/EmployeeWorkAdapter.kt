package com.udit.ClientConnect.utils



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.button.MaterialButton
import com.udit.ClientConnect.R
import com.udit.ClientConnect.databinding.ItemViewEmployeeWorkBinding


class EmployeeWorkAdapter(
    val onProgressButtonClicked: (Work, MaterialButton) -> Unit,
    val onCompleteButtonClicked: (Work, MaterialButton) -> Unit
) : RecyclerView.Adapter<EmployeeWorkAdapter.EmployeeWorksViewHolder>() {
    class EmployeeWorksViewHolder(val binding: ItemViewEmployeeWorkBinding):ViewHolder(binding.root)
    val diffUtil = object:DiffUtil.ItemCallback<Work>(){
        override fun areItemsTheSame(oldItem: Work, newItem: Work): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Work, newItem: Work): Boolean {

            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil) // this is used to do comparsion on back thread;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeWorksViewHolder {
        return EmployeeWorksViewHolder(ItemViewEmployeeWorkBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: EmployeeWorksViewHolder, position: Int) {

        val works = differ.currentList[position]
        val isExpended = works.expanded
        holder.binding.apply {
            tvTitle.text = works.workTitle
            tvDate.text = works.workLastDate
            tvWorkDescription.text = works.workDescription
            when(works.workStatus){
                "1" -> holder.binding.tvStatus.text = "Pending"
                "2" -> holder.binding.tvStatus.text ="Progress"
                "3" -> holder.binding.tvStatus.text = "Completed"
            }
            tvWorkDescription.visibility = if(isExpended) View.VISIBLE else View.GONE
            workDesc.visibility = if(isExpended) View.VISIBLE else View.GONE
            btnProgress.visibility = if(isExpended) View.VISIBLE else View.GONE
            btnCompleted.visibility = if(isExpended) View.VISIBLE else View.GONE

            if(tvStatus.text =="Progress" || tvStatus.text == "Completed"){
                btnProgress.text = "In Progress"
                btnProgress.setTextColor(ContextCompat.getColor(root.context , R.color.Light5))
            }
            if(tvStatus.text == "Completed"){
                btnCompleted.text = "Work Completed"
                btnCompleted.setTextColor(ContextCompat.getColor(root.context,R.color.Light5))
            }

            constraintLayout.setOnClickListener {
                isAnyExpanded(position)
                works.expanded = !works.expanded
                notifyItemChanged(position,0)
            }

            btnProgress.setOnClickListener {
                onProgressButtonClicked(works,btnProgress)
            }
            btnCompleted.setOnClickListener {
                onCompleteButtonClicked(works,btnCompleted)
            }

        }
    }

    private fun isAnyExpanded(position: Int) {
        val expandeditemIndex = differ.currentList.indexOfFirst { it.expanded }
        if(expandeditemIndex >= 0 && expandeditemIndex != position){
            differ.currentList[expandeditemIndex].expanded = false
            notifyItemChanged(expandeditemIndex,0)
        }
    }

    override fun onBindViewHolder(
        holder: EmployeeWorksViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty() && payloads[0] == 0){
            holder.binding.apply {
                tvWorkDescription.visibility = View.GONE
                workDesc.visibility = View.GONE
                btnProgress.visibility = View.GONE
                btnCompleted.visibility = View.GONE
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

}