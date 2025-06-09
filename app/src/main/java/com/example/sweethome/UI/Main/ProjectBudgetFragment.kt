package com.example.sweethome.UI.Main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ProjectBudgetFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var stateSpinner: Spinner
    private lateinit var addressEditText: EditText
    private lateinit var budgetEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var delButton: Button
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private var projectId: String? = null
    private val statusOptions = listOf("active", "archive")
    private val stateOptions = listOf("Private", "Commerce")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        drawerLayout = requireActivity().findViewById(R.id.drawerLayout)
        titleEditText = view.findViewById(R.id.titleEditText)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        stateSpinner = view.findViewById(R.id.stateSpinner)
        addressEditText = view.findViewById(R.id.addressEditText)
        budgetEditText = view.findViewById(R.id.budgetEditText)
        saveButton = view.findViewById(R.id.saveProjectButton)
        delButton = view.findViewById(R.id.deleteProjectButton)
        projectId = arguments?.getString("projectId")

        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        requireActivity().findViewById<NavigationView>(R.id.navigationView)
            .setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.ProjectFragment -> {
                        findNavController().navigate(R.id.ProjectFragment)
                        true
                    }

                    R.id.HomeFragment -> {
                        findNavController().navigate(R.id.HomeFragment)
                        true
                    }

                    else -> false
                }
            }

        projectId = arguments?.getString("projectId")
        projectId?.let { LoadProject(it) }//Загружаем уже известные поля проекта через id,что передали при переходе

        statusSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        stateSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stateOptions)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        saveButton.setOnClickListener {
            SaveProject()
        }

        delButton.setOnClickListener {
            DeleteProject(projectId)
        }
    }

    private fun LoadProject(id: String) {
        firestore.collection("projects").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val project = doc.toObject(Project::class.java)
                    if (project != null) {
                        FillInfo(project)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load project", Toast.LENGTH_SHORT).show()
            }
    }

    private fun FillInfo(project: Project) {
        toolbar.title = project.title
        titleEditText.setText(project.title)
        addressEditText.setText(project.address)
        if (project.budget.toString() == "null"){ budgetEditText.setText("0") } else budgetEditText.setText((project.budget).toString())

        val statusIndex = statusOptions.indexOf(project.status.lowercase())
        statusSpinner.setSelection(statusIndex)

        val stateIndex = stateOptions.indexOf(project.state)
        stateSpinner.setSelection(stateIndex)
    }

    private fun SaveProject() {
        val updatedProject = hashMapOf<String, Any?>(
            "title" to titleEditText.text.toString(),
            "status" to statusOptions[statusSpinner.selectedItemPosition],
            "state" to stateOptions[stateSpinner.selectedItemPosition],
            "address" to addressEditText.text.toString(),
            "budget" to budgetEditText.text.toString().toDouble()
        )

        projectId?.let {
            firestore.collection("projects").document(it)
                .update(updatedProject)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun DeleteProject(projectId: String?) {
        if (projectId == null) return

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Project")
            .setMessage("This action will delete project and all tasks. Continue?")
            .setPositiveButton("Yes") { _, _ ->
                val batch = firestore.batch()
                val projectDel = firestore.collection("projects").document(projectId)
                batch.delete(projectDel)
                firestore.collection("tasks")
                    .whereEqualTo("projectId", projectId)
                    .get().addOnSuccessListener { querySnapshot ->
                        for (taskDel in querySnapshot.documents) {
                            batch.delete(taskDel.reference)
                        }
                        batch.commit().addOnSuccessListener {
                                Toast.makeText(requireContext(), "Project deleted", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.HomeFragment)
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Failed to delete project", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }


}