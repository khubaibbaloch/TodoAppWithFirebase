package com.example.todolist

import android.content.Context
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.todolist.databinding.ActivityEditTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Edit_Task : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var taskId: String
    private var selectedColorResId: Int = R.color.white
    private var previouslySelectedButton: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_task)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        db = FirebaseFirestore.getInstance()
        taskId = intent.getStringExtra("taskId").toString()
        fetchTaskDetails(taskId)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.colorOption1.setOnClickListener { onColorOptionClicked(R.color.white, binding.colorOption1) }
        binding.colorOption2.setOnClickListener { onColorOptionClicked(R.color.colorOption1, binding.colorOption2) }
        binding.colorOption3.setOnClickListener { onColorOptionClicked(R.color.colorOption2, binding.colorOption3) }
        binding.colorOption4.setOnClickListener { onColorOptionClicked(R.color.colorOption3, binding.colorOption4) }
        binding.colorOption5.setOnClickListener { onColorOptionClicked(R.color.colorOption4, binding.colorOption5) }


        binding.updateTask.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
                val updatedTitle = binding.editTitle.text.toString()
                val updatedDetail = binding.editDetail.text.toString()
                updateTask(taskId, updatedTitle, updatedDetail,selectedColorResId)
                if (isOffline()){
                finish()
            }
        }
        binding.CancelTask.setOnClickListener { onBackPressed() }
    }

    private fun fetchTaskDetails(taskId: String) {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .collection("userTasks").document(taskId)
            .addSnapshotListener { snapshot, exception ->
                binding.progressBar.visibility = View.GONE
                if (exception != null) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to fetch task details: ${exception.message}", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity if an error occurs
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Extract task details from the snapshot and populate UI
                    val taskTitle = snapshot.getString("tittle")
                    val taskDescription = snapshot.getString("detail")
                    binding.editTitle.setText(taskTitle)
                    binding.editDetail.setText(taskDescription)
                    val currentColorResId = snapshot.getLong("color")?.toInt()
                    currentColorResId?.let { colorResId ->
                        // Find the corresponding color option button and set it as selected
                        val selectedButton = when (colorResId) {
                            R.color.white -> binding.colorOption1
                            R.color.colorOption1 -> binding.colorOption2
                            R.color.colorOption2 -> binding.colorOption3
                            R.color.colorOption3 -> binding.colorOption4
                            R.color.colorOption4 -> binding.colorOption5
                            else -> null
                        }
                        selectedButton?.let { onColorOptionClicked(colorResId, it) }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity if task not found
                }
            }
    }


    private fun updateTask(taskId: String, updatedTitle: String, updatedDetail: String, selectedColorResId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .collection("userTasks").document(taskId)
            .update(mapOf(
                "tittle" to updatedTitle,
                "detail" to updatedDetail,
                "color" to selectedColorResId // Add the selectedColorResId parameter to the update map
            ))
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after successful update
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to update task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isOffline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo == null || !networkInfo.isConnected
    }
    private fun onColorOptionClicked(colorResId: Int, selectedButton: View) {
        // Store the previously selected button
        val oldSelectedButton = previouslySelectedButton

        // Check if the selected button is different from the previously selected button
        if (selectedButton != oldSelectedButton) {
            // Reset the background drawable of the previously selected button
            oldSelectedButton?.let { button ->
                val previousBackgroundResId = when (button) {
                    binding.colorOption1 -> R.drawable.color_1_bacground
                    binding.colorOption2 -> R.drawable.color_2_bacground
                    binding.colorOption3 -> R.drawable.color_3_bacground
                    binding.colorOption4 -> R.drawable.color_4_bacground
                    binding.colorOption5 -> R.drawable.color_5_bacground
                    else -> R.drawable.text_feild_background
                }
                button.background = ContextCompat.getDrawable(this, previousBackgroundResId)
            }
        }

        // Update the background drawable of the selected color option button
        val backgroundDrawableResId = when (selectedButton) {
            binding.colorOption1 -> R.drawable.color_1_bacground_selected
            binding.colorOption2 -> R.drawable.color_2_bacground_selected
            binding.colorOption3 -> R.drawable.color_3_bacground_selected
            binding.colorOption4 -> R.drawable.color_4_bacground_selected
            binding.colorOption5 -> R.drawable.color_5_bacground_selected
            else -> R.drawable.text_feild_background
        }
        selectedButton.background = ContextCompat.getDrawable(this, backgroundDrawableResId)

        // Update the selectedColorResId
        selectedColorResId = colorResId

        // Set the current selected button as the previously selected button for the next click
        previouslySelectedButton = selectedButton
    }



}