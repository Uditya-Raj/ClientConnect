package com.udit.ClientConnect.utils



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.udit.ClientConnect.databinding.ItemViewWorksBinding


class WorksAdapter(val onUnassignedButtonClicked: (Work) -> Unit) : RecyclerView.Adapter<WorksAdapter.WorksViewHolder>() {
    class WorksViewHolder(val binding: ItemViewWorksBinding):ViewHolder(binding.root)
    val diffUtil = object:DiffUtil.ItemCallback<Work>(){
        override fun areItemsTheSame(oldItem: Work, newItem: Work): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Work, newItem: Work): Boolean {

            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil) // this is used to do comparsion on back thread;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorksViewHolder {
        return WorksViewHolder(ItemViewWorksBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: WorksViewHolder, position: Int) {
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
             btnWorkDone.visibility = if(isExpended) View.VISIBLE else View.GONE

             constraintLayout.setOnClickListener {
                 isAnyExpanded(position)
                 works.expanded = !works.expanded
                 notifyItemChanged(position,0)
             }
             btnWorkDone.setOnClickListener {
                 onUnassignedButtonClicked(works)
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
        holder: WorksViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty() && payloads[0] == 0){
            holder.binding.apply {
                tvWorkDescription.visibility = View.GONE
                workDesc.visibility = View.GONE
                btnWorkDone.visibility = View.GONE
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

}