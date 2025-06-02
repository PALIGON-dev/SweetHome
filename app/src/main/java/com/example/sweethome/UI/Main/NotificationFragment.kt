package com.example.sweethome.UI.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private lateinit var AddTask: Button
    private lateinit var Back: Button

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
        AddTask = view.findViewById(R.id.addTaskButton)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val projectMap = viewModel.projects.value ?: emptyMap()
            adapter.updateTasks(tasks, projectMap)
        }

        viewModel.projects.observe(viewLifecycleOwner) { projectMap ->
            val tasks = viewModel.tasks.value ?: emptyList()
            adapter.updateTasks(tasks, projectMap)
        }

        viewModel.loadUserTasksAndProjects()

        AddTask.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), ActivityAddTask::class.java)
            startActivity(intent)
            requireActivity().finish()
        })
    }
}
