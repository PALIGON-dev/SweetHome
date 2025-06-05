package com.example.sweethome.Data.Repository

import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.Data.Remote.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class UserRepo {
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUserProjects(onResult: (List<Project>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return


        db.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                val projectIds = user?.projects ?: emptyList()

                if (projectIds.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                //Получаем проекты по id
                db.collection("projects")
                    .whereIn(FieldPath.documentId(), projectIds)
                    .get()
                    .addOnSuccessListener { projectSnapshots ->
                        val projects = projectSnapshots.documents.mapNotNull {
                            it.toObject(Project::class.java)
                        }
                        onResult(projects)
                    }
            }
    }
}
