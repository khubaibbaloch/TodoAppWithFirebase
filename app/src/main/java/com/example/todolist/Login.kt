package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.todolist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        binding.signUp.setOnClickListener {
            startActivity(Intent(this,Sign_up::class.java))
            finish()
        }
        binding.forgetPassword.setOnClickListener {
            startActivity(Intent(this,Reset_Password::class.java))
            finish()
        }
        binding.Login.setOnClickListener {
            startActivity(Intent(this,Navigation_screens::class.java))
            finish()
        }
        binding.Login.setOnClickListener {
            val email = binding.checkEmail.text.toString()
            val pasasword = binding.checkPassword.text.toString()
            logincheck(email,pasasword)
        }
    }
    private fun logincheck(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.VISIBLE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // User is authenticated and email is verified
                        startActivity(Intent(this, Navigation_screens::class.java))
                        finish()
                    } else {
                        // User is either not authenticated or email is not verified
                        if (user != null) {
                            // Send email verification link
                            user.sendEmailVerification()
                                .addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(baseContext, "Verification email sent. Please verify your email.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(baseContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(baseContext, "Authentication failed or email is not verified.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Authentication failed
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }


    }
}