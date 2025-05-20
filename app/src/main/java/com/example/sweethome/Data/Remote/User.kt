package com.example.sweethome.Data.Remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp


data class User(
    @DocumentId val id: String = "", //Для дальнейшей авторизации
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null, //Аватар при Firebase Auth
    val projects: List<String> = emptyList(), //Id проектов
    @ServerTimestamp val createdAt: Timestamp? = null, //Дата регистрации
    @ServerTimestamp val lastActive: Timestamp? = null, //Последняя активность
)

