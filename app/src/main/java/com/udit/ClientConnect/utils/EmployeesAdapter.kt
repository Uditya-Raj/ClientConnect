package com.udit.ClientConnect.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.udit.ClientConnect.EmployeesFragmentDirections
import com.udit.ClientConnect.databinding.EmployeeItemViewBinding


class EmployeesAdapter: RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {
     class EmployeeViewHolder(val binding: EmployeeItemViewBinding):ViewHolder(binding.root)
     val diffUtil = object:DiffUtil.ItemCallback<User>(){
         override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
         }

         override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {

             return oldItem == newItem
         }

     }

    val differ = AsyncListDiffer(this,diffUtil) // this is used to do comparsion on back thread;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
      return EmployeeViewHolder(EmployeeItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val empdata = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(empdata.userImage).into(empProfile)
            tvEmployeeName.text = empdata.userName
        }
        holder.itemView.setOnClickListener {
            val action = EmployeesFragmentDirections.actionEmployeesFragmentToWorksFragment(empdata)
            Navigation.findNavController(it).navigate(action)
        }
    }


}