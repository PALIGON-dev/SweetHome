package com.example.sweethome.UI.Main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ProjectFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var Tasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var btnPlanner: Button
    private lateinit var btnPayments: Button
    private lateinit var btnService: Button
    private lateinit var addBtn: Button
    private var projectId: String? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Tasks = view.findViewById(R.id.taskRecyclerView)
        toolbar = view.findViewById(R.id.toolbar)
        drawerLayout = requireActivity().findViewById(R.id.drawerLayout)
        btnPlanner = view.findViewById(R.id.btnPlanner)
        btnPayments = view.findViewById(R.id.btnPayment)
        btnService = view.findViewById(R.id.btnService)
        addBtn = view.findViewById(R.id.addBtn)
        projectId = arguments?.getString("projectId")

        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        requireActivity().findViewById<NavigationView>(R.id.navigationView).setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.projectBudgetFragment -> {
                    val bundle = Bundle().apply {
                        putString("projectId", projectId)
                    }
                    findNavController().navigate(R.id.projectBudgetFragment, bundle)
                    true
                }
                R.id.HomeFragment -> {
                    findNavController().navigate(R.id.HomeFragment)
                    true
                }
                else -> false
            }
        }

        taskAdapter = TaskAdapter(emptyList(), emptyMap(), viewModel)
        Tasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }

        viewModel.tasks.observe(viewLifecycleOwner) {  //Устанавливаем Planner и подписываем на изменения
            updateTasksByCategory("Planner")
        }

        btnPlanner.setOnClickListener(View.OnClickListener{
            updateTasksByCategory("Planner")
        })

        btnPayments.setOnClickListener(View.OnClickListener{
            updateTasksByCategory("Payments")
        })

        btnService.setOnClickListener(View.OnClickListener{
            updateTasksByCategory("Service")
        })

        addBtn.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.addTaskFragment)
        })
    }

    override fun onResume() {
        super.onResume()
        projectId?.let { id -> LoadProject(id) }
        viewModel.loadUserTasksAndProjects()
    }

     fun LoadProject(id: String) {
        firestore.collection("projects").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val project = doc.toObject(Project::class.java)
                    if (project != null) {
                        toolbar.setTitle(project.title)
                        toolbar.setLogo(R.color.white)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load project", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTasksByCategory(category: String) {
        val filteredTasks = viewModel.filterTasksByProjectIdAndCategory(projectId, category)
        taskAdapter.updateTasks(filteredTasks, viewModel.projects.value ?: emptyMap())
    }


}