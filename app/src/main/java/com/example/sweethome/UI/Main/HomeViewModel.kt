package com.example.sweethome.UI.Main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.Data.Repository.UserRepo

class HomeViewModel : ViewModel() {
    private val userRepository = UserRepo()

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private var allProjects: List<Project> = emptyList()

    fun loadUserProjects() {
        userRepository.getCurrentUserProjects { projectList ->
            allProjects = projectList
            _projects.value = allProjects
        }
    }

    fun filterProjectsByState(state: String) {
        val filtered = allProjects.filter { it.status == state }
        _projects.value = filtered
    }
}
