package com.udit.ClientConnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udit.ClientConnect.auth.SignInActivity
import com.udit.ClientConnect.utils.EmployeesAdapter
import com.udit.ClientConnect.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udit.ClientConnect.databinding.FragmentEmployeesBinding


class EmployeesFragment : Fragment() {
    private lateinit var  binding: FragmentEmployeesBinding
    private lateinit var employeesAdapter: EmployeesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
         binding = FragmentEmployeesBinding.inflate(layoutInflater)
         binding.btnEmployeeFrag.setOnClickListener {
             findNavController().navigate(R.id.action_employeesFragment_to_worksFragment)
         }

         binding.tbEmployee.setOnMenuItemClickListener { it ->
             when (it.itemId) {
                 R.id.logout -> {

                     showLogOutDailog()
                     true

                 }

                 else -> false
             }

         }
         prepareRvForEmployeeAdapter()
         showAllEmployees()
        return binding.root
    }

    private fun showAllEmployees() {
        FirebaseDatabase.getInstance().getReference("Employee").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val emplist = arrayListOf<User>()
                for(employees in snapshot.children){
                    Log.d("Firebase", "Raw data: ${employees.value}")
                    val currentUser = employees.getValue(User::class.java)
                    Log.d("Firebasedata", "Raw data: $currentUser.value}")
                    if (currentUser != null) {
                        emplist.add(currentUser)
                    }
                }
                Log.d("array",emplist.toString())
                employeesAdapter.differ.submitList(emplist)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun prepareRvForEmployeeAdapter() {
        employeesAdapter = EmployeesAdapter()
        binding.rvEmployees.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = employeesAdapter
        }
    }

    private fun showLogOutDailog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()
        builder.setTitle("Log Out").setMessage("Are you Sure")
            .setPositiveButton("Yes"){_ ,_->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(requireContext(),SignInActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("No"){_,_->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)


    }


}