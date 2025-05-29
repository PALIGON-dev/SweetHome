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

    fun loadUserProjects() {
        userRepository.getCurrentUserProjects { projectList ->
            _projects.value = projectList
        }
    }
}