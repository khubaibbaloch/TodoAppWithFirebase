package com.example.todolist

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.databinding.DataBindingUtil
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.Calendar
import kotlin.properties.Delegates

class Add_Task : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private  var db = Firebase.firestore
    private var selectedColorResId: Int = R.color.white
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_task)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.colorOption1.setOnClickListener { onColorOptionClicked(R.color.white) }
        binding.colorOption2.setOnClickListener { onColorOptionClicked(R.color.colorOption1) }
        binding.colorOption3.setOnClickListener { onColorOptionClicked(R.color.colorOption2) }
        binding.colorOption4.setOnClickListener { onColorOptionClicked(R.color.colorOption3) }
        binding.colorOption5.setOnClickListener { onColorOptionClicked(R.color.colorOption4) }


        binding.addTask.setOnClickListener {
            val tittle = binding.addTitle.text.toString()
            val detail = binding.addDetail.text.toString()
            val completed = false

            addUserData(tittle,detail,completed,selectedColorResId)
            if (isOffline()){
                finish()
            }
        }
    }
    private fun addUserData(tittle: String, detail: String, completed: Boolean,color:Int) {
        if (tittle.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this, "Title and detail cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userData = User(null,tittle, detail, completed,color)
            binding.progressBar.visibility =View.VISIBLE
            // Add the userData collection for the user
            db.collection("users").document(userId).collection("userTasks")
                .add(userData)
                .addOnSuccessListener { documentReference ->
                    binding.progressBar.visibility =View.GONE
                    Toast.makeText(this, "User data added successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,Navigation_screens::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    binding.progressBar.visibility =View.GONE
                    Toast.makeText(this, "Failed to add user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            binding.progressBar.visibility =View.GONE
            // User is not authenticated, handle accordingly
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isOffline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo == null || !networkInfo.isConnected
    }

    private fun onColorOptionClicked(colorResId: Int) {
        // Reset the background drawable of all color option buttons
        binding.colorOption1.background = ContextCompat.getDrawable(this, R.drawable.color_1_bacground)
        binding.colorOption2.background = ContextCompat.getDrawable(this, R.drawable.color_2_bacground)
        binding.colorOption3.background = ContextCompat.getDrawable(this, R.drawable.color_3_bacground)
        binding.colorOption4.background = ContextCompat.getDrawable(this, R.drawable.color_4_bacground)
        binding.colorOption5.background = ContextCompat.getDrawable(this, R.drawable.color_5_bacground)

        // Update the background drawable of the selected color option button
        val selectedButton = when (colorResId) {
            R.color.white -> binding.colorOption1
            R.color.colorOption1 -> binding.colorOption2
            R.color.colorOption2 -> binding.colorOption3
            R.color.colorOption3 -> binding.colorOption4
            R.color.colorOption4 -> binding.colorOption5
            else -> null
        }

        // Set the background drawable for the selected button based on its number
        selectedButton?.let { button ->
            val backgroundDrawableResId = when (button) {
                binding.colorOption1 -> R.drawable.color_1_bacground_selected
                binding.colorOption2 -> R.drawable.color_2_bacground_selected
                binding.colorOption3 -> R.drawable.color_3_bacground_selected
                binding.colorOption4 -> R.drawable.color_4_bacground_selected
                binding.colorOption5 -> R.drawable.color_5_bacground_selected
                else -> null
            }
            button.background = ContextCompat.getDrawable(this, backgroundDrawableResId ?: R.drawable.color_1_bacground_selected)
        }

        // Update the selectedColorResId
        selectedColorResId = colorResId
    }



}

