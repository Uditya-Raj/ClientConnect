package com.udit.ClientConnect.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.udit.ClientConnect.utils.User
import com.udit.ClientConnect.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.udit.ClientConnect.databinding.AccountDialogBinding
import com.udit.ClientConnect.databinding.ActivitySignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private var userImageUri: Uri? = null
    private lateinit var userType:String
    private var selectImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        userImageUri = it
        binding.ivUserImage.setImageURI(userImageUri)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ivUserImage.setOnClickListener {
                selectImage.launch("image/*")
            }
        }
        binding.radioGroup.setOnCheckedChangeListener{ _,checkedId ->
            userType = findViewById<RadioButton>(checkedId).text.toString()
            Log.d("Radio button",userType)
        }
        binding.btnRegister.setOnClickListener {
          //  Utils.showDialog(this@SignUpActivity)
            createUser()
        }
        binding.txtSignIn.setOnClickListener{
            startActivity(Intent(this@SignUpActivity,SignInActivity::class.java))
            finish()
        }

    }

    private fun createUser() {
       // Utils.showDialog(this)
        val name = binding.EdtName.text.toString()
        val email = binding.EdtEmail.text.toString()
        val password = binding.EdtPassword.text.toString()
        val ConfirmPassword = binding.EdtConfirmPass.text.toString()

        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && ConfirmPassword.isNotEmpty()){
            //
            if(userImageUri == null){
                Toast.makeText(this,"Select Profile Image",Toast.LENGTH_SHORT).show()
            }else if(password == ConfirmPassword){
                  Utils.showDialog(this@SignUpActivity)
                   uploadImageUri(name,email,password)
            }else{
                Toast.makeText(this, "Password Not Matched !!!", Toast.LENGTH_SHORT).show()
            }
        }else{

            // toast message for every field
            if(name.isEmpty()){
                Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show()
            }else if(email.isEmpty()){
                Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()){
                Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Confirm password Required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageUri(name: String, email: String, password: String) {
        val filename = UUID.randomUUID().toString()+".jpg"
        val storageReference = FirebaseStorage.getInstance().getReference("Profile").child(filename)

        lifecycleScope.launch {
            try {
                val uploadTask = storageReference.putFile(userImageUri!!).await()
                if(uploadTask.task.isSuccessful){
                   val downloadUrl = storageReference.downloadUrl.await()
                    saveUserData(name,email,password,downloadUrl)
                }else{
                    Utils.hideDialog()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@SignUpActivity, "Upload Failed: ${uploadTask.task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch(e:Exception){
                Utils.hideDialog()
                 Toast.makeText(this@SignUpActivity,"Upload Failed:${e.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(name: String, email: String, password: String, downloadUrl: Uri?) {

            lifecycleScope.launch {
                val db = FirebaseDatabase.getInstance()

                    try {
                        val firebaseAuth = FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, password).await()
                        if (firebaseAuth.user != null) {
                            // Email Verification function
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    val dialog =
                                        AccountDialogBinding.inflate(LayoutInflater.from(this@SignUpActivity))
                                    val alertDialog = AlertDialog.Builder(this@SignUpActivity)
                                        .setView(dialog.root)
                                        .create()
                                  //  Utils.hideDialog()
                                    alertDialog.show()
                                    dialog.btnOk.setOnClickListener {
                                        alertDialog.dismiss()
                                        startActivity(
                                            Intent(
                                                this@SignUpActivity, SignInActivity::class.java))
                                        finish()
                                    }
                                }
                            val saveUserType = if (userType == "Boss") "Boss" else "Employee"
                            val uid = firebaseAuth.user?.uid.toString()
                            val userInfo = User(userId=uid, userName=name, userEmail=email, userPassword=password, userImage=downloadUrl.toString())
                            db.getReference(saveUserType).child(uid).setValue(userInfo).await()

                        } else {
                            Utils.hideDialog()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Sign Up Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Utils.hideDialog()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SignUpActivity,
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }


    }

    private fun showToast(context:Context,message:String) {

            Toast.makeText(context,message , Toast.LENGTH_SHORT).show()
    }
}