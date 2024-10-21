package com.udit.ClientConnect.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.udit.ClientConnect.BossMainActivity
import com.udit.ClientConnect.EmployeeMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udit.ClientConnect.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = FirebaseAuth.getInstance().currentUser?.uid
            if(currentUser != null){
                lifecycleScope.launch {
                        try{

                                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(object:
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val userType = if(snapshot.child("Boss").hasChild(currentUser)){
                                            "Boss"
                                        }else if(snapshot.child("Employee").hasChild(currentUser)){
                                            "Employee"
                                        }else{
                                            "Unknown"
                                        }
                                        when(userType){
                                            "Boss" ->{
                                                startActivity(Intent(this@SplashActivity,BossMainActivity::class.java))
                                                finish()
                                            }
                                            "Employee"->{
                                                startActivity(Intent(this@SplashActivity, EmployeeMainActivity::class.java))
                                                finish()
                                            }
                                            "Unknown" -> {
                                                startActivity(Intent(this@SplashActivity,BossMainActivity::class.java))
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                        Toast.makeText(this@SplashActivity,error.message, Toast.LENGTH_SHORT).show()
                                    }

                                })

                        }catch (e:Exception){
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@SplashActivity,e.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                }

            }else{
                startActivity(Intent(this@SplashActivity,SignInActivity::class.java))
                finish()
            }

            }, 3000)

    }
}