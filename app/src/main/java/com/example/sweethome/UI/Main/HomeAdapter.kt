package com.example.sweethome.UI.Main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.Data.Remote.Project
import com.example.sweethome.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class HomeAdapter(
    private var items: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<HomeAdapter.HorizontalViewHolder>() {

    class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val startAt: TextView = itemView.findViewById(R.id.startAtTextView)
        val state: TextView = itemView.findViewById(R.id.stateTextView)
    }

    //Форматируем дату из Timestamp в строку "dd.MM.yyyy"
    private fun formatTimestamp(timestamp: Timestamp?): String {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.format(timestamp.toDate())
        } else {
            "нет даты"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_adapter, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.startAt.text = formatTimestamp(item.startAt)
        holder.state.text = item.state

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<Project>) {
        this.items = newList
        notifyDataSetChanged()
    }
}