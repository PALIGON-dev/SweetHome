package com.example.sweethome.UI.Main

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
    private var projectMap: Map<String, String> //Id проекта и его название
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskProject: TextView = itemView.findViewById(R.id.taskProject)
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
    }

    override fun getItemCount(): Int = taskList.size

    fun updateTasks(newTasks: List<Task>, newProjectMap: Map<String, String>) {
        taskList = newTasks
        projectMap = newProjectMap
        notifyDataSetChanged()
    }
}

