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
        findNavController().navigate(R.id.ProjectFragment, bundle)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.ProjectRecycler)
        val addBtn = view.findViewById<Button>(R.id.addButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapt

        addBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), ActivityAddProject::class.java)
            startActivity(intent)//не закрываем активность для обновления списка
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserProjects()//Обновление для перехода после добавления
    }
}