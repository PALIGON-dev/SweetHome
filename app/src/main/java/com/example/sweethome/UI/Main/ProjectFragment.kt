package com.example.sweethome.UI.Main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class ProjectFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var Tasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private var projectMap: Map<String, String> = emptyMap()
    private var projectId: String? = null

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

        viewModel.loadUserTasksAndProjects()

        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            projectMap = projects
            UpdateAdapterForProject(projectId)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            UpdateAdapterForProject(projectId)
        }
    }

    fun UpdateAdapterForProject(projectId: String?) {
        if (projectId == null) return
        val filteredTasks = viewModel.filterTasksByProjectId(projectId)
        taskAdapter.updateTasks(filteredTasks, projectMap)
    }

}