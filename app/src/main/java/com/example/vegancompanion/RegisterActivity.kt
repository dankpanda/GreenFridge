package com.example.vegancompanion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vegancompanion.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private var database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // Registration validation
        binding.signUpButton.setOnClickListener{
            signUpValidation()
        }

        binding.signIn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun signUpValidation(){
        if(TextUtils.isEmpty(binding.emailEditTextSignUp.text)){
            binding.emailEditTextSignUp.error = "Please enter email address"
            binding.emailEditTextSignUp.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditTextSignUp.text.toString()).matches()){
            binding.emailEditTextSignUp.error = "Please enter a valid email address"
            binding.emailEditTextSignUp.requestFocus()
            return
        }

        if(TextUtils.isEmpty(binding.passwordTextSignUp.text)){
            binding.passwordTextSignUp.error = "Please enter password"
            binding.confirmPasswordTextSignUp.setText("")
            binding.passwordTextSignUp.requestFocus()
            return
        }

        if(TextUtils.isEmpty(binding.confirmPasswordTextSignUp.text)){
            binding.passwordTextSignUp.error = "Please confirm password"
            binding.passwordTextSignUp.setText("")
            binding.passwordTextSignUp.requestFocus()
            return
        }

        if(binding.passwordTextSignUp.text.toString() != binding.confirmPasswordTextSignUp.text.toString()){
            binding.passwordTextSignUp.error = "Passwords did not match"
            binding.passwordTextSignUp.setText("")
            binding.confirmPasswordTextSignUp.setText("")
            binding.passwordTextSignUp.requestFocus()
            return
        }

        if(binding.passwordTextSignUp.text.toString().length < 8){
            binding.passwordTextSignUp.error = "Password needs to be at least 8 characters long"
            binding.passwordTextSignUp.setText("")
            binding.confirmPasswordTextSignUp.setText("")
            binding.passwordTextSignUp.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(binding.emailEditTextSignUp.text.toString(), binding.passwordTextSignUp.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Sign up success",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Sign up failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                }
    }

}


