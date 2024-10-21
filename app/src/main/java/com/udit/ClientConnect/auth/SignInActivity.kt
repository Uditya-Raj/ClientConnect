package com.udit.ClientConnect.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.udit.ClientConnect.BossMainActivity
import com.udit.ClientConnect.EmployeeMainActivity
import com.udit.ClientConnect.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udit.ClientConnect.databinding.ActivitySignInBinding
import com.udit.ClientConnect.databinding.ForgetPasswordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.loginEdtEmail.text.toString()
            val password = binding.loginEdtPass.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){

                loginUser(email,password)
            }else{
                  if(email.isEmpty()){
                      Toast.makeText(this@SignInActivity,"email Required",Toast.LENGTH_SHORT).show()
                  }
                   if(password.isEmpty()){
                       Toast.makeText(this@SignInActivity,"password Required",Toast.LENGTH_SHORT).show()
                   }

            }

        }
        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this@SignInActivity,SignUpActivity::class.java))
            finish()
        }
        binding.txtForgetPass.setOnClickListener {
            showForgetPasswordDialog()
        }

    }

    private fun showForgetPasswordDialog() {
        val dialog = ForgetPasswordBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this).setView(dialog.root).create()
        alertDialog.show()
       // dialog.forgetEmail.requestFocus()

        dialog.btnForgetPassword.setOnClickListener {
            val email = dialog.forgetEmail.text.toString()
            alertDialog.dismiss()
            resetPassword(email)
        }
        dialog.backToSignIn.setOnClickListener {
            alertDialog.dismiss()

        }
    }

    private fun resetPassword(email: String) {
        lifecycleScope.launch {
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@SignInActivity,"Check your email to reset password",Toast.LENGTH_SHORT).show()
                }
            }catch(e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@SignInActivity,e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun loginUser(email: String, password: String) {
        Utils.showDialog(this@SignInActivity)
        lifecycleScope.launch {
            val firebaseAuth = FirebaseAuth.getInstance()
            try{
                val authResult =  firebaseAuth.signInWithEmailAndPassword(email,password).await()
                // check Email verification
                val verifyEmail = firebaseAuth.currentUser?.isEmailVerified
                if(verifyEmail == true) {
                    if (authResult.user != null) {

                        val currentUser = authResult.user!!.uid
                        FirebaseDatabase.getInstance().getReference()
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userType =
                                        if (snapshot.child("Boss").hasChild(currentUser)) {
                                            "Boss"
                                        } else if (snapshot.child("Employee")
                                                .hasChild(currentUser)
                                        ) {
                                            "Employee"
                                        } else {
                                            "Unknown"
                                        }
                                    when (userType) {
                                        "Boss" -> {
                                            startActivity(
                                                Intent(
                                                    this@SignInActivity,
                                                    BossMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        "Employee" -> {
                                            startActivity(
                                                Intent(
                                                    this@SignInActivity,
                                                    EmployeeMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                        "Unknown" ->{
                                            startActivity(Intent(this@SignInActivity,BossMainActivity::class.java))
                                            finish()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Utils.hideDialog()
                                    Toast.makeText(
                                        this@SignInActivity,
                                        error.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            })
                    }
                }else{
                    withContext(Dispatchers.Main){
                        Utils.hideDialog()
                        Toast.makeText(this@SignInActivity,"Email not verified",Toast.LENGTH_SHORT).show()
                    }
                }


            }catch (e:Exception){
                Utils.hideDialog()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@SignInActivity,"User Not found!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}