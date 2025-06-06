package com.example.sweethome.UI.Main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.Data.Remote.Task
import com.example.sweethome.R

class TaskAdapter(
    private var taskList: List<Task>,
    private var projectMap: Map<String, String>, //Id проекта и его название
    private val viewModel: TaskViewModel
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskProject: TextView = itemView.findViewById(R.id.taskProject)
        val checkBox: ImageView = itemView.findViewById(R.id.taskIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_adapter, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title
        holder.taskProject.text = projectMap[task.projectId] ?: "Unknown project"

        updateTaskStyle(holder, task)

        holder.checkBox.setOnClickListener {
            viewModel.updateTaskStatus(task) { updatedTask ->
                taskList = taskList.toMutableList().apply {
                    set(position, updatedTask)
                }
                notifyItemChanged(position)
            }
    }
        }

    override fun getItemCount(): Int = taskList.size

    fun updateTasks(newTasks: List<Task>, newProjectMap: Map<String, String>) {
        taskList = newTasks
        projectMap = newProjectMap
        notifyDataSetChanged()
    }

    @SuppressLint("ResourceAsColor")
    private fun updateTaskStyle(holder: TaskViewHolder, task: Task) {
        if (task.status == "archieve") {
            holder.taskTitle.setTextColor(R.color.grey)
            holder.taskProject.setTextColor(R.color.grey)
            holder.checkBox.setImageResource(R.drawable.checkbox)
        }
    }

}

