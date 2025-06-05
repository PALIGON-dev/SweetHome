package com.example.sweethome.UI.Main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.R
import java.time.LocalDate

class DateAdapter(
    private val dates: List<LocalDate>,
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedDate: LocalDate = LocalDate.now()

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayOfWeek)
        val dateText: TextView = view.findViewById(R.id.dayOfMonth)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date_adapter, parent, false)
        return DateViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]

        val dayOfWeekShort = date.dayOfWeek
            .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)
            .take(3)

        holder.dayText.text = dayOfWeekShort
        holder.dateText.text = date.dayOfMonth.toString()

        if (date == selectedDate) {
            holder.itemView.setBackgroundResource(R.drawable.squared_background)
        } else {
            holder.itemView.setBackgroundResource(R.color.white)
        }

        holder.itemView.setOnClickListener {
            selectedDate = date
            notifyDataSetChanged()
            onDateClick(date)
        }
    }

    override fun getItemCount(): Int = dates.size

    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
        notifyDataSetChanged()
    }

}



