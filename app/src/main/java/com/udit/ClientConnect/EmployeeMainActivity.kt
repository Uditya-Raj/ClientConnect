package com.udit.ClientConnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.udit.ClientConnect.auth.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.udit.ClientConnect.databinding.ActivityEmployeeBinding

class EmployeeMainActivity : AppCompatActivity() {
    private  lateinit var  binding : ActivityEmployeeBinding
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
}