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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sweethome.Data.Remote.Task
import com.example.sweethome.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale


class ActivityAddTask : AppCompatActivity() {

    private lateinit var Date: EditText
    private lateinit var Title: EditText
    private lateinit var Project: Spinner
    private lateinit var Category: Spinner
    private lateinit var Back: Button
    private lateinit var Add: Button
    private lateinit var viewModel: HomeViewModel
    private val projectIdMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        Date = findViewById(R.id.TaskEditDate)
        Title = findViewById(R.id.TaskEditTitle)
        Project = findViewById(R.id.TaskProjectSpinner)
        Category = findViewById(R.id.TaskCategorySpinner)
        Back = findViewById(R.id.backTaskButton)
        Add = findViewById(R.id.addTaskButton)

        Date.inputType = InputType.TYPE_NULL
        Date.isFocusable = false //Отключаем ввод даты,только через календарь

        Date.setOnClickListener {
            showDatePicker()
        }
        //Установка спинера категорий
        val categories = listOf("Planner", "Payment", "Service")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Category.adapter = adapter

        //Установка спинера проектов через LiveData
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.projects.observe(this) { projectList ->
            val projectNames = projectList.map { it.title }
            projectIdMap.clear()
            projectList.forEach { project ->
                projectIdMap[project.title] = project.id
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Project.adapter = adapter
        }
        viewModel.loadUserProjects()

        Add.setOnClickListener(View.OnClickListener setOnClickListener@{
            val title = Title.text.toString()
            val dateString = Date.text.toString()
            val category = Category.selectedItem.toString()
            val projectName = Project.selectedItem?.toString() ?: return@setOnClickListener
            val projectId = projectIdMap[projectName] ?: return@setOnClickListener
            val parsedDate = dateFormat.parse(dateString)
            addTaskToProject(projectId, title, parsedDate, category)
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

    fun addTaskToProject(projectId: String,Title: String, Date: Date, Category: String) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("AddTask", "Пользователь не авторизован")
            return
        }

        val testTask = Task(
            projectId = projectId,
            category = Category,
            title = Title,
            text = "",
            status = "active",
            date = Date
        )

        firestore.collection("tasks")
            .add(testTask)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Log.e("AddTask", "Ошибка при добавлении задания: ${e.message}")
            }
    }
}
