package com.example.todolist

import android.content.Context
import android.content.Intent
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.todolist.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        binding =  FragmentProfileBinding.inflate(inflater, container, false)
        showUserInfo()
        binding.logout.setOnClickListener {
            showLogoutConfirmationDialog()
           // LogoutAccount()
        }
        binding.delete.setOnClickListener {
            showDeleteAccountConfirmationDialog()
           // DeleteAccount()
        }

        return binding.root
    }

    private fun DeleteAccount() {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            user?.delete()
                ?.addOnSuccessListener {
                    // Account deleted successfully, navigate to login screen and clear task stack
                    Toast.makeText(requireContext(), "Account deleted successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), Login::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
                ?.addOnFailureListener { e ->
                    // Failed to delete account, display error message
                    Toast.makeText(requireContext(), "Failed to delete account: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // User is offline, show a message indicating they cannot delete the account
            Toast.makeText(requireContext(), "Cannot delete account while offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun LogoutAccount() {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            // User is online, proceed with logout
            auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                auth.signOut()
                Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()
                // Start login activity and clear the task stack
                startActivity(Intent(requireContext(), Login::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        } else {
            // User is offline, show a message indicating they cannot logout
            Toast.makeText(requireContext(), "Cannot logout while offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUserInfo() {
        val currentUser = auth.currentUser
        binding.userName.text = currentUser?.email ?: "No email available"
        binding.textView6.text = currentUser?.displayName ?: "No user Name available"
        val profileImageUri = currentUser?.photoUrl
        if (profileImageUri != null) {
            Glide.with(this)
                .load(profileImageUri)
                .placeholder(R.drawable.profile_image)
                .into(binding.profileImage)
            Log.d("TAG", profileImageUri.toString())
        }
    }
    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Logout Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            LogoutAccount()
        }
        alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        alertDialogBuilder.create().show()
    }

    private fun showDeleteAccountConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Delete Account Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to delete your account?")
        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            DeleteAccount()
        }
        alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        alertDialogBuilder.create().show()
    }

}