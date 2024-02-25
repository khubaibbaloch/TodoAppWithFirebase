package com.example.todolist

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class CompletedTasksAdapter : RecyclerView.Adapter<CompletedTasksAdapter.CompletedTaskViewHolder>() {
    private var completedTasksList: List<User> = emptyList()

    inner class CompletedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textView3)
        val detail = itemView.findViewById<TextView>(R.id.textView4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.complet_task_item, parent, false)
        return CompletedTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) {
        val user = completedTasksList[position]
        holder.title.text = user.tittle
        holder.detail.text = user.detail
        user.color.let {
            val colorTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, it))
            ViewCompat.setBackgroundTintList(holder.itemView, colorTintList)
        }
        val backgroundColor = holder.itemView.backgroundTintList?.defaultColor ?: 0

        // Check if the background color is not white
        val isBackgroundColorWhite = backgroundColor == Color.WHITE

        // Set title text color to white if background color is not white, otherwise keep it as is
        val titleTextColor = if (!isBackgroundColorWhite) {
            Color.WHITE
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.main_color) // Set your default text color here
        }

        // Set the title text color
        holder.title.setTextColor(titleTextColor)

        // Set detail text color to black only if background color is white, otherwise keep it as is
        val detailTextColor = if (isBackgroundColorWhite) {
            ContextCompat.getColor(holder.itemView.context, android.R.color.black)
        } else {
            titleTextColor // Use the same color as the title text color
        }

        // Set the detail text color
        holder.detail.setTextColor(detailTextColor)
    }

    override fun getItemCount(): Int {
        return completedTasksList.size
    }

    fun submitList(completedTasks: List<User>) {
        completedTasksList = completedTasks
        notifyDataSetChanged()
    }
}