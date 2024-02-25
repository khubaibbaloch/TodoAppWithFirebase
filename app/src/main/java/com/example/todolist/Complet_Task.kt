package com.example.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.CompletTaskItemBinding
import com.example.todolist.databinding.FragmentCompletTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class Complet_Task : Fragment() {
    private lateinit var binding: FragmentCompletTaskBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var completedTasksAdapter: CompletedTasksAdapter
  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentCompletTaskBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        completedTasksAdapter = CompletedTasksAdapter()

        binding.completedTasksRecyclerView.apply {
          layoutManager = LinearLayoutManager(requireContext())
          adapter = completedTasksAdapter
        }
        retrieveCompletedTasks()

        return binding.root
  }

    private fun retrieveCompletedTasks() {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("users").document(userId).collection("userTasks")
            .whereEqualTo("completed", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val completedTasks = mutableListOf<User>()
                for (document in querySnapshot.documents) {
                    val task = document.toObject<User>()
                    task?.let { completedTasks.add(it) }
                }
                completedTasksAdapter.submitList(completedTasks)
                if (completedTasks.isEmpty()) {
                    binding.noCompletedTasksTextView.visibility = View.VISIBLE
                } else {
                    binding.noCompletedTasksTextView.visibility = View.GONE
                }
                    // Hide progress bar after data is loaded
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                // Hide progress bar in case of failure
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to retrieve completed tasks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}