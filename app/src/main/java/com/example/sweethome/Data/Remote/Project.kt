package com.example.sweethome.Data.Remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Project(
    @DocumentId val id: String = "", //Автогенерация id
    val title: String = "",
    val userId: String = "",
    val status: String = "active", //Или archive
    val address: String = "", // Адрес объекта
    val budget: Double? = null, // Общий бюджет (опционально)
    val serviceTasks: List<Task> = emptyList(), //Обслуживание
    val paymentTasks: List<Task> = emptyList(), //Платежи
    val plannerTasks: List<Task> = emptyList(), //Общие
    val startAt: Timestamp? = null, //Дата начала
    val endAt: Timestamp? = null, //Дата конца
    @ServerTimestamp val createdAt: Timestamp? = null,//Дата создания
)