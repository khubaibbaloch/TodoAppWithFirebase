package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.todolist.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null ) {
            if (currentUser.isEmailVerified){
                startActivity(Intent(this, Navigation_screens::class.java))
                finish()
            }else{
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }else{
            startActivity(Intent(this, Sign_up::class.java))
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.getStarted.setOnClickListener {
            startActivity(Intent(this,Sign_up::class.java))
            finish()
        }
    }}