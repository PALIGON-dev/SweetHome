package com.example.sweethome.Data.Remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Task(
    @DocumentId val id: String = "", //Автогенерация id
    val projectId: String = "",
    val category: String = "", //Planner,payment,service
    val title: String = "",
    val text: String = "",
    val status: String = "active", //Archive
    val date: Date? = null, //Дата выполнения
    @ServerTimestamp val createdAt: Timestamp? = null, //Дата создания
)