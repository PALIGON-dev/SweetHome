package com.example.sweethome.UI.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private val adapt = HomeAdapter(emptyList()) { project ->
        val bundle = Bundle().apply {
            putString("projectId", project.id)
        }
        findNavController().navigate(R.id.action_HomeFragment_to_ProjectFragment,bundle)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.projects.observe(viewLifecycleOwner) { list ->
            adapt.updateData(list)
        }

        viewModel.loadUserProjects()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.ProjectRecycler)
        val addBtn = view.findViewById<Button>(R.id.addButton)
        val activeTab = view.findViewById<TextView>(R.id.activeTab)
        val archiveTab = view.findViewById<TextView>(R.id.archiveTab)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapt

        activeTab.setOnClickListener {
            viewModel.filterProjectsByState("active")
            activeTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            archiveTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        }

        archiveTab.setOnClickListener {
            viewModel.filterProjectsByState("archive")
            activeTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            archiveTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        addBtn.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_addProjectFragment)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserProjects()//Обновление для перехода после добавления
    }
}