package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.todolist.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class Reset_Password : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reset_password)
        auth = FirebaseAuth.getInstance()
        binding.signUp.setOnClickListener {
            startActivity(Intent(this,Sign_up::class.java))
            finish()
        }
        binding.signIn.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
            finish()
        }
        binding.resetPassword.setOnClickListener {
            val email = binding.checkEmail.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.progressBar.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "Password reset email sent", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}