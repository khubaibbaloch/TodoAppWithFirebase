package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.FragmentAllTaskBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.util.Calendar

class All_Task : Fragment() {
    private lateinit var binding: FragmentAllTaskBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var userTasksRef: CollectionReference
    private lateinit var userTasksListener: ListenerRegistration
    private val tasksPendingDeletion = HashSet<String>()
    private val taskDeletionHandler = Handler(Looper.getMainLooper())
    private lateinit var snackbar: Snackbar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllTaskBinding.inflate(inflater, container, false)
        db = Firebase.firestore
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        userTasksRef = db.collection("users").document(userId).collection("userTasks")
        binding.addTask.setOnClickListener {
            startActivity(Intent(requireContext(), Add_Task::class.java))
        }
        dayOfMonth()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        retrieveUserDataFromFirestore()
    }

    override fun onStart() {
        binding.progressBar.visibility = View.GONE
        super.onStart()
        retrieveUserDataFromFirestore()
        startTaskListener()

    }

    override fun onStop() {
        binding.progressBar.visibility = View.GONE
        super.onStop()
        userTasksListener.remove()
    }


    private fun retrieveUserDataFromFirestore() {
        userTasksRef.get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<User>()
                for (document in result) {
                    val taskId = document.id
                    val userData = document.toObject<User>().copy(taskId = taskId)
                    userList.add(userData)
                }
                displayUserData(userList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to retrieve user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startTaskListener() {
        userTasksListener = userTasksRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(requireContext(), "Error fetching tasks: ${exception.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val userList = mutableListOf<User>()
                for (doc in snapshot.documents) {
                    val taskId = doc.id
                    val userData = doc.toObject<User>()?.copy(taskId = taskId)
                    userData?.let { userList.add(it) }
                }
                displayUserData(userList)
            }
        }
    }

    private fun displayUserData(userList: MutableList<User>) {
        // Inside your fragment where you set up the RecyclerView
        val adapter = UserAdapter(
            userList,
            { taskId -> navigateToEditScreen(taskId) },
            { taskId -> showDeleteSnackbar(taskId) }, // Pass taskId to show delete snackbar
            { taskId -> completeTask(taskId) } // Pass the completed action
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun showDeleteSnackbar(taskId: String) {
        val adapter = binding.recyclerView.adapter as? UserAdapter
        val taskIndex = adapter?.indexOfTaskId(taskId)

        taskIndex?.let { index ->
            // Remove the task from the UI immediately
            val removedItem = adapter.userList[index]


            adapter.removeAt(index)
            binding.root.let { rootView ->
                snackbar = Snackbar.make(
                    rootView,
                    "Task deleted",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction("Undo") {

                    // Cancel the scheduled task deletion
                    cancelTaskDeletion(taskId)

                    // Add the task back to the UI
                    adapter.userList.add(index, removedItem)

                    adapter.notifyItemInserted(index)

                }
                snackbar.show()

                // Schedule task deletion after 5 seconds if not undone
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    // Check if the task is still pending deletion
                    if (tasksPendingDeletion.contains(taskId)) {
                        deleteTask(taskId)
                    }
                }, 3000)

                // Add taskId to pending deletion list
                tasksPendingDeletion.add(taskId)
            }}
    }
    private fun cancelTaskDeletion(taskId: String) {
        // Remove taskId from pending deletion list
        tasksPendingDeletion.remove(taskId)
        // Show a message indicating that the task deletion was canceled
        Toast.makeText(requireContext(), "Task deletion canceled", Toast.LENGTH_SHORT).show()
    }

    private fun deleteTask(taskId: String) {
        // Remove taskId from pending deletion list
        tasksPendingDeletion.remove(taskId)

        // Perform the task deletion from the RecyclerView
        tasksPendingDeletion.remove(taskId)
        val taskIndex = (binding.recyclerView.adapter as? UserAdapter)?.indexOfTaskId(taskId)
        taskIndex?.let {
            (binding.recyclerView.adapter as? UserAdapter)?.removeAt(it)
        }

        // Now delete the task from the database
        db.collection("users").document(userId).collection("userTasks")
            .document(taskId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to delete task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun completeTask(taskId: String) {
        db.collection("users").document(userId).collection("userTasks")
            .document(taskId)
            .update("completed", true)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Task completed successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to complete task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToEditScreen(taskId: String) {
        val intent = Intent(requireContext(), Edit_Task::class.java).apply {
            putExtra("taskId", taskId)
        }
        startActivity(intent)
    }

    private fun dayOfMonth() {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        binding.currentDate.text = dayOfMonth.toString()
    }
}

