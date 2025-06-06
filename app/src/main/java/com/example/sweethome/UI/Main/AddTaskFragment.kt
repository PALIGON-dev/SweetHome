package com.example.sweethome.UI.Main

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sweethome.Data.Remote.Task
import com.example.sweethome.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AddTaskFragment : Fragment() {

    private lateinit var Date: EditText
    private lateinit var Title: EditText
    private lateinit var Project: Spinner
    private lateinit var Category: Spinner
    private lateinit var Back: Button
    private lateinit var Add: Button
    private lateinit var viewModel: HomeViewModel
    private val projectIdMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Date = view.findViewById(R.id.ProjectEditDate)
        Title = view.findViewById(R.id.ProjectEditTitle)
        Project = view.findViewById(R.id.ProjectTypeSpinner)
        Category = view.findViewById(R.id.TaskCategorySpinner)
        Back = view.findViewById(R.id.backProjectButton)
        Add = view.findViewById(R.id.addProjectButton)

        Date.inputType = InputType.TYPE_NULL
        Date.isFocusable = false //Отключаем ввод даты,только через календарь

        Date.setOnClickListener {
            showDatePicker()
        }
        //Установка спинера категорий
        val categories = listOf("Planner", "Payment", "Service")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, categories)
        adapter.setDropDownViewResource(R.layout.spinner_item_white)
        Category.adapter = adapter
        Category.setPopupBackgroundResource(R.color.white)

        //Установка спинера проектов через LiveData
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.projects.observe(viewLifecycleOwner) { projectList ->
            val projectNames = projectList.map { it.title }
            projectIdMap.clear()
            projectList.forEach { project ->
                projectIdMap[project.title] = project.id
            }

            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, projectNames)
            adapter.setDropDownViewResource(R.layout.spinner_item_white)
            Project.adapter = adapter
            Project.setPopupBackgroundResource(R.color.white)
        }
        viewModel.loadUserProjects()

        Add.setOnClickListener(View.OnClickListener setOnClickListener@{
            val title = Title.text.toString()
            val dateString = Date.text.toString()
            val category = Category.selectedItem.toString()
            val projectName = Project.selectedItem?.toString() ?: return@setOnClickListener
            val projectId = projectIdMap[projectName] ?: return@setOnClickListener

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
                addTaskToProject(projectId, title, timestamp, category)
            } else {
                Log.e("AddTask", "Ошибка парсинга даты")
            }
        })

        Back.setOnClickListener {
            findNavController().navigate(R.id.action_addTaskFragment_to_NotificationFragment)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
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

    fun addTaskToProject(projectId: String, Title: String, Date: Timestamp, Category: String) {
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
                findNavController().navigate(R.id.action_addTaskFragment_to_NotificationFragment)
                Toast.makeText(activity, "Task added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("AddTask", "Ошибка при добавлении задания: ${e.message}")
            }
    }
}