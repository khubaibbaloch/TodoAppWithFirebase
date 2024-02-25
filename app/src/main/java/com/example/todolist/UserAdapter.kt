package com.example.todolist


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    var userList: MutableList<User>,
    private val onEditClickListener: (String) -> Unit,
    private val onDeletedClickListener: (String) -> Unit,
    private val onCompletedClickListener: (String) -> Unit):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textView3)
        val detail: TextView = itemView.findViewById(R.id.textView4)
        val editTaskButton: ImageView = itemView.findViewById(R.id.edit_data)
        val deletTaskButton: ImageView = itemView.findViewById(R.id.delet_task)
        val completTask: ImageView = itemView.findViewById(R.id.completed_Task)

        init {
            editTaskButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userList[position]
                    val taskId = user.taskId ?: "" // Retrieve the task ID from the User object
                    onEditClickListener(taskId)
                }
            }
            deletTaskButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userList[position]
                    val taskId = user.taskId ?: "" // Retrieve the task ID from the User object
                    onDeletedClickListener(taskId)
                }
            }
            completTask.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userList[position]
                    val taskId = user.taskId ?: "" // Retrieve the task ID from the User object
                    onCompletedClickListener(taskId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.title.text = user.tittle
        holder.detail.text = user.detail
        user.color.let {
            val colorTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, it))
            ViewCompat.setBackgroundTintList(holder.itemView, colorTintList)
        }
        // Get the background color of the layout
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

        // Set background tint for completed tasks
        val iconResource = if (user.completed) R.drawable.task_completed else R.drawable.task_uncompleted
        holder.completTask.setImageResource(iconResource)
        holder.editTaskButton.isEnabled = !user.completed
        holder.editTaskButton.imageTintList = if (user.completed) ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray)) else null
    }


    override fun getItemCount(): Int {
        return userList.size
    }
    fun indexOfTaskId(taskId: String): Int {
        return userList.indexOfFirst { it.taskId == taskId }
    }



    fun removeAt(index: Int) {
        if (index >= 0 && index < userList.size) {
            userList.removeAt(index)
            notifyItemRemoved(index)
        }
    }



}




