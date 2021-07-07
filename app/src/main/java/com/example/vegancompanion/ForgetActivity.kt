package com.example.vegancompanion

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vegancompanion.databinding.ActivityForgetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signIn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.sendEmailButton.setOnClickListener{
            sendEmail()
        }
    }

    private fun sendEmail(){
        if(TextUtils.isEmpty(binding.emailForgetPasswordEditText.text)){
            binding.emailForgetPasswordEditText.error = "Please enter email address"
            binding.emailForgetPasswordEditText.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailForgetPasswordEditText.text.toString()).matches()){
            binding.emailForgetPasswordEditText.error = "Please enter a valid email address"
            binding.emailForgetPasswordEditText.requestFocus()
            return
        }

        val emailAddress = binding.emailForgetPasswordEditText.text.toString()
        Firebase.auth.sendPasswordResetEmail(emailAddress)
        Toast.makeText(this,"Password reset instructions has been sent to the specified email",Toast.LENGTH_LONG).show()
    }


}