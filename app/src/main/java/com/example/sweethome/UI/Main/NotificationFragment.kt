package com.example.sweethome.UI.Main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.Data.Remote.Task
import com.example.sweethome.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NotificationFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.tasksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskAdapter(emptyList(), emptyMap())
        recyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val projectMap = viewModel.projects.value ?: emptyMap()
            adapter.updateTasks(tasks, projectMap)
        }

        viewModel.projects.observe(viewLifecycleOwner) { projectMap ->
            val tasks = viewModel.tasks.value ?: emptyList()
            adapter.updateTasks(tasks, projectMap)
        }

        viewModel.loadUserTasksAndProjects()
    }

    fun addTestTaskToProject(projectId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("AddTask", "Пользователь не авторизован")
            return
        }

        val testTask = Task(
            projectId = projectId,
            category = "planner",
            title = "Тестовое задание",
            text = "Это описание тестового задания",
            status = "active",
            date = Timestamp.now()
        )

        firestore.collection("tasks")
            .add(testTask)
            .addOnSuccessListener {
                Log.d("AddTask", "Тестовое задание добавлено успешно")
            }
            .addOnFailureListener { e ->
                Log.e("AddTask", "Ошибка при добавлении задания: ${e.message}")
            }
    }

}
