package com.udit.ClientConnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udit.ClientConnect.databinding.FragmentWorksBinding
import com.udit.ClientConnect.utils.Utils
import com.udit.ClientConnect.utils.Work
import com.udit.ClientConnect.utils.WorksAdapter


class WorksFragment : Fragment() {
    val employeeDetails by navArgs<WorksFragmentArgs>()
    private lateinit var worksAdapter: WorksAdapter
     private lateinit var  binding : FragmentWorksBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorksBinding.inflate(layoutInflater)

        binding.fabAssignWork.setOnClickListener {
            val action = WorksFragmentDirections.actionWorksFragmentToAssignWorkFragment(employeeDetails.employeeData)
            findNavController().navigate(action)
        }
        val empName = employeeDetails.employeeData.userName
        binding.tbEmployeeWork.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.tbEmployeeWork.title = empName
        prepareRvForWorkAdapter()
        showAllWorks()

        return binding.root
    }

    private fun showAllWorks() {
      //  Utils.showDialog(requireContext())
       val bossId = FirebaseAuth.getInstance().currentUser?.uid
        val userId = employeeDetails.employeeData.userId
        val workId = bossId+userId
        FirebaseDatabase.getInstance().getReference("Works").child(workId).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 val workList = ArrayList<Work>()
                for(allwork in snapshot.children){
                    val work = allwork.getValue(Work::class.java)  // here value get typecasted in User form
                    if (work != null) {
                        workList.add(work)
                    }
                }
                worksAdapter.differ.submitList(workList)
              //  Utils.hideDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet Implemented")
            }

        })
    }

    private fun prepareRvForWorkAdapter(){
        worksAdapter = WorksAdapter()
        binding.rvWorks.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = worksAdapter
        }
    }


}