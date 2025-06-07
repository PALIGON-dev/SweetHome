package com.example.sweethome.UI.Main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sweethome.Data.Remote.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.ZoneId

class TaskViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _projects = MutableLiveData<Map<String, String>>()
    val projects: LiveData<Map<String, String>> = _projects

    fun loadUserTasksAndProjects() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("projects")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { projectSnapshot ->
                val projectMap = mutableMapOf<String, String>()
                val projectIds = mutableListOf<String>()

                for (doc in projectSnapshot) {
                    val id = doc.id
                    val title = doc.getString("title") ?: "No title"
                    projectMap[id] = title
                    projectIds.add(id)
                }

                _projects.value = projectMap

                if (projectIds.isNotEmpty()) {
                    //Получаем все задачи, где projectId из projectIds
                    firestore.collection("tasks")
                        .whereIn("projectId", projectIds)
                        .get()
                        .addOnSuccessListener { taskSnapshot ->
                            val tasks = taskSnapshot.mapNotNull { doc ->
                                doc.toObject(Task::class.java).copy(id = doc.id)
                            }
                            _tasks.value = tasks
                        }
                }
                else{ }
            }
    }

    fun updateTaskStatus(task: Task, onComplete: (Task) -> Unit) {
        val newStatus = if (task.status == "archieve") "active" else "archieve"
        val updatedTask = task.copy(status = newStatus)//Установили новий статус задачи

        FirebaseFirestore.getInstance().collection("tasks")
            .document(task.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                onComplete(updatedTask) //Сообщаем адаптеру об успешном обновлении
            }
    }


    fun filterTasksByDate(tasks: List<Task>, selectedDate: LocalDate): List<Task> {
        return tasks.filter { task ->
            task.date?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() == selectedDate
        }
    }

    fun filterTasksByProjectId(projectId: String): List<Task> {
        return _tasks.value?.filter { it.projectId == projectId } ?: emptyList()
    }
}
