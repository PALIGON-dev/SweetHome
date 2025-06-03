package com.example.sweethome.UI.Main

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.Data.Remote.Task
import com.example.sweethome.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class ActivityAddProject : AppCompatActivity() {

    private lateinit var Date: EditText
    private lateinit var Title: EditText
    private lateinit var Address: EditText
    private lateinit var Type: Spinner
    private lateinit var Back: Button
    private lateinit var Add: Button
    private lateinit var viewModel: HomeViewModel
    private val projectIdMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        Date = findViewById(R.id.ProjectEditDate)
        Title = findViewById(R.id.ProjectEditTitle)
        Type = findViewById(R.id.ProjectTypeSpinner)
        Address = findViewById(R.id.ProjectEditAddress)
        Back = findViewById(R.id.backProjectButton)
        Add = findViewById(R.id.addProjectButton)

        Date.inputType = InputType.TYPE_NULL
        Date.isFocusable = false //Отключаем ввод даты,только через календарь

        Date.setOnClickListener {
            showDatePicker()
        }
        //Установка спинера категорий
        val categories = listOf("Private", "Commerce")
        val adapter = ArrayAdapter(this, R.layout.spinner_item_white, categories)
        adapter.setDropDownViewResource(R.layout.spinner_item_white)
        Type.adapter = adapter
        Type.setPopupBackgroundResource(R.color.white)

        Add.setOnClickListener(View.OnClickListener setOnClickListener@{
            val title = Title.text.toString()
            val dateString = Date.text.toString()
            val address = Address.text.toString()
            val type = Type.selectedItem.toString()

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            //Преобразуем String в Date
            val parsedDate = try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                Log.e("AddTask", "Неверный формат даты: $dateString")
                null
            }

            //Преобразуем Date в Timestamp
            if (parsedDate != null && title != "") {
                val timestamp = Timestamp(parsedDate)
                addProjectToFirestore(title,address, type, timestamp)
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            } else {
                Log.e("AddTask", "Ошибка парсинга даты")
            }
        })

        Back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Activity().finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                Date.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun addProjectToFirestore(title: String, address: String, type: String, startDate: Timestamp?) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("AddProject", "Пользователь не авторизован")
            return
        }

        val newProject = Project(
            title = title,
            userId = currentUser.uid,
            status = "active",
            state = type,
            address = address,
            startAt = startDate
        )

        firestore.collection("projects")
            .add(newProject)
            .addOnSuccessListener { documentRef ->
                val projectId = documentRef.id
                firestore.collection("users")
                    .document(currentUser.uid)
                    .update("projects", FieldValue.arrayUnion(projectId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "AddProject", "Не удалось добавить проект: ${e.message}"
                        )
                    }
                Toast.makeText(this, "Проект добавлен", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }
    }
}
