package com.udit.ClientConnect

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.udit.ClientConnect.auth.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udit.ClientConnect.databinding.ActivityEmployeeBinding
import com.udit.ClientConnect.utils.EmployeeWorkAdapter
import com.udit.ClientConnect.utils.Work

class EmployeeMainActivity : AppCompatActivity() {
    private  lateinit var  binding : ActivityEmployeeBinding
    private lateinit var employeeWorkAdapter:EmployeeWorkAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tbEmployee.setOnMenuItemClickListener { it ->
            when (it.itemId) {
                R.id.logout -> {

                    showLogOutDailog()
                    true

                }

                else -> false
            }

        }
        prepareRvEmployeeAdapter()
        showEmployeeWorks()

    }

    @SuppressLint("SuspiciousIndentation")
    private fun showEmployeeWorks() {
        val empId = FirebaseAuth.getInstance().currentUser?.uid
        val workRef = FirebaseDatabase.getInstance().getReference("Works")
            workRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(workId in snapshot.children){
                    if(workId.key?.contains(empId!!) == true){
                        val empWorkref = workRef.child(workId.key!!)
                        empWorkref.addValueEventListener(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val workList = ArrayList<Work>()
                                for(works in snapshot.children){
                                    val work = works.getValue(Work::class.java)
                                    if (work != null) {
                                        workList.add(work)
                                    }

                                }
                                employeeWorkAdapter.differ.submitList(workList)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun prepareRvEmployeeAdapter() {
         employeeWorkAdapter = EmployeeWorkAdapter(::onProgressButtonClicked,::onCompleteButtonClicked)
        binding.rvEmployeeWork.apply {
            layoutManager = LinearLayoutManager(this@EmployeeMainActivity,LinearLayoutManager.VERTICAL,false)
            adapter = employeeWorkAdapter
        }
    }

    private fun showLogOutDailog() {
        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()
        builder.setTitle("Log Out").setMessage("Are you Sure want to logout")
            .setPositiveButton("Yes"){_ ,_->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .setNegativeButton("No"){_,_->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

    @SuppressLint("SetTextI18n")
    private fun onProgressButtonClicked(works:Work, progressButton:MaterialButton) {
        if (progressButton.text != "In Progress") {
            val builder = AlertDialog.Builder(this)
            val alertDialog = builder.create()
            builder.setTitle("Starting Work").setMessage("Are you Starting this work?")
                .setPositiveButton("Yes") { _, _ ->
                    progressButton.apply {
                        text = "In Progress"
                        setTextColor(
                            ContextCompat.getColor(
                                this@EmployeeMainActivity,
                                R.color.Light5
                            )
                        )
                    }
                    updateStatus(works, "2")
                }
                .setNegativeButton("No") { _, _ ->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false)
        }else{
            Toast.makeText(this,"Work is in progress or Completed..",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatus(works: Work, status: String) {
        val empId = FirebaseAuth.getInstance().currentUser?.uid
        val workRef = FirebaseDatabase.getInstance().getReference("Works")
        workRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(workId in snapshot.children){
                    if(workId.key?.contains(empId!!) == true){
                        val empWorkref = workRef.child(workId.key!!)
                        empWorkref.addListenerForSingleValueEvent(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(allwork in snapshot.children){
                                    val work = allwork.getValue(Work::class.java)
                                    if(works.workId == work?.workId){
                                         empWorkref.child(allwork.key!!).child("workStatus").setValue(status)
                                    }


                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun onCompleteButtonClicked(works:Work,completedButton:MaterialButton) {
        if (completedButton.text != "Work Completed") {
            val builder = AlertDialog.Builder(this)
            val alertDialog = builder.create()
            builder.setTitle("Work Completed").setMessage("Are you Sure, Work is Completed ?")
                .setPositiveButton("Yes") { _, _ ->
                    completedButton.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this@EmployeeMainActivity,
                                R.color.Light5
                            )
                        )
                    }
                    updateStatus(works, "3")
                }
                .setNegativeButton("No") { _, _ ->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false)
        }else{
            Toast.makeText(this,"Work is in progress or Completed..",Toast.LENGTH_SHORT).show()
        }
    }
}