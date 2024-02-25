package com.example.todolist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.todolist.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Sign_up : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        binding.signIn.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
        }
        binding.getProfileImage.setOnClickListener {
            selectImage()
        }
        binding.Resgister.setOnClickListener {
            val userName = binding.getUserName.text.toString()
            val userLastName = binding.getEmail.text.toString()
            val userPassword = binding.getPassword.text.toString()
            val userConfirmPassword = binding.getConfirmPassword.text.toString()

            registerUser(userName, userLastName, userPassword, userConfirmPassword,profileImageUri)
        }
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            profileImageUri = data.data
            Glide.with(this)
                .load(profileImageUri)
                .placeholder(R.drawable.profile_image) // Placeholder
                .into(binding.getProfileImage)
        }
    }
    private fun registerUser(userName: String, email: String, password: String, confirmPassword: String, profileImageUri: Uri?) {
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (profileImageUri == null) {
            Toast.makeText(this, "Profile image is required", Toast.LENGTH_SHORT).show()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .setPhotoUri(profileImageUri)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                startActivity(Intent(this, Login::class.java))
                                finish()
                                Toast.makeText(baseContext, "Account created successfully.", Toast.LENGTH_SHORT).show()
                                sendVerificationEmail()
                                binding.progressBar.visibility = View.GONE
                            } else {
                                Toast.makeText(baseContext, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
    }
    private fun sendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnSuccessListener {
                Toast.makeText(applicationContext, "Verification email sent", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                if (user.isEmailVerified) {
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
            }
            ?.addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                // Failed to send verification email
                Toast.makeText(applicationContext, "Failed to send verification email: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}