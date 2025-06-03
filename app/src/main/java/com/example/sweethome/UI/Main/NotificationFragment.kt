package com.example.sweethome.UI.Main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.Data.Remote.Task
import com.example.sweethome.R
import java.time.LocalDate

class NotificationFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var datesRecyclerView: RecyclerView
    private lateinit var dateAdapter: DateAdapter
    private lateinit var addTaskButton: Button
    private lateinit var MonthText: TextView
    private var allTasks: List<Task> = emptyList()
    private var projectMap: Map<String, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView)
        datesRecyclerView = view.findViewById(R.id.DateRecycler)
        addTaskButton = view.findViewById(R.id.addProjectButton)
        MonthText = view.findViewById(R.id.monthLabel)

        taskAdapter = TaskAdapter(emptyList(), emptyMap())
        tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }

        val dates = generate360Days()
        dateAdapter = DateAdapter(dates) { selectedDate ->
            val filteredTasks = viewModel.filterTasksByDate(allTasks, selectedDate)
            taskAdapter.updateTasks(filteredTasks, projectMap)
            updateMonthLabel(selectedDate)
        }

        datesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
        }

        val today = LocalDate.now()
        val todayIndex = dates.indexOf(today)
        dateAdapter.setSelectedDate(today)
        updateMonthLabel(today)
        datesRecyclerView.scrollToPosition(todayIndex)

        viewModel.loadUserTasksAndProjects()//Загружаем все задачи для сортировки

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            allTasks = tasks
            updateAdapterForToday()
        }

        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            projectMap = projects
            updateAdapterForToday()
        }

        addTaskButton.setOnClickListener {
            val intent = Intent(requireContext(), ActivityAddTask::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun updateAdapterForToday() {
        // Обновляем адаптер, если и задачи, и проекты загружены
        if (allTasks.isNotEmpty() && projectMap.isNotEmpty()) {
            val today = LocalDate.now()
            val todayTasks = viewModel.filterTasksByDate(allTasks, today)
            taskAdapter.updateTasks(todayTasks, projectMap)
        }
    }

    // Возвращает список из 360 дней, 180 до сегодня и 180 после
    private fun generate360Days(): List<LocalDate> {
        val today = LocalDate.now()
        return (-180..180).map { today.plusDays(it.toLong()) }
    }

    private fun updateMonthLabel(date: LocalDate) {
        val monthName = date.month
            .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
        MonthText.text = "$monthName ${date.year}"
    }

}

