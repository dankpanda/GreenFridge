package com.example.vegancompanion

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.vegancompanion.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // On click listener for register button
        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        // On click listener for sign in button
        binding.signInButton.setOnClickListener {
            doLogin()
        }

    }

    // Function for logging user in
    private fun doLogin() {
        if (TextUtils.isEmpty(binding.emailEditText.text)) {
            binding.emailEditText.error = "Please enter email address"
            binding.emailEditText.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString()).matches()) {
            binding.emailEditText.error = "Please enter a valid email address"
            binding.emailEditText.requestFocus()
            return
        }

        if (TextUtils.isEmpty(binding.passwordText.text)) {
            binding.passwordText.error = "Please enter password"
            binding.passwordText.requestFocus()
            return
        }

        // Checks whether email and password are correct
        auth.signInWithEmailAndPassword(
            binding.emailEditText.text.toString(),
            binding.passwordText.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Logged in as ${FirebaseAuth.getInstance().currentUser!!.email}", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }

            }
    }

    // Update app based on login result
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Toast.makeText(this, "Logged in as ${FirebaseAuth.getInstance().currentUser!!.email}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            binding.emailEditText.error = "Incorrect email or password"
        }
    }

    // Checks whether user is already logged in on startup
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }

    // Automatically login user on startup
    private fun reload() {
        auth.currentUser!!.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
                Toast.makeText(
                    this,
                    "Signed in as ${FirebaseAuth.getInstance().currentUser!!.email}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}