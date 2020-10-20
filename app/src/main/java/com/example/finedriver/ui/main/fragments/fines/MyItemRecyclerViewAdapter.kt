package com.example.finedriver.ui.main.fragments.fines

import android.graphics.Color
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.finedriver.R
import com.example.finedriver.data.finesData.model.FinesItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyItemRecyclerViewAdapter(
    private val values: List<FinesItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_fines, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.carNumberText.text = item.tzNumber
        holder.timeText.text = item.updatedAt
        holder.fineAmount.text = item.cost.toString() + " грн."
        var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        holder.dataText.text = LocalDate.parse(item.date).format(formatter)
        if (item.payed) {
            holder.fineStatus.text = "СПЛАЧЕНО"
            holder.fineStatus.setTextColor(Color.parseColor("#219653"))
            holder.fineAmount.setTextColor(Color.parseColor("#219653"))
        }
        else{
            holder.fineStatus.text = "НЕ СПЛАЧЕНО"
            holder.fineStatus.setTextColor(Color.parseColor("#EC0101"))
            holder.fineAmount.setTextColor(Color.parseColor("#EC0101"))

        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fineStatus: TextView = view.findViewById(R.id.fine_status)
        val articleNumber: TextView = view.findViewById(R.id.article_number)
        val articleText: TextView = view.findViewById(R.id.article_text)
        val dataText: TextView = view.findViewById(R.id.data_text)
        val carModelText: TextView = view.findViewById(R.id.car_model_text)
        val carNumberText: TextView = view.findViewById(R.id.car_number_text)
        val fineAmount: TextView = view.findViewById(R.id.fine_amount)
        val timeText: TextView = view.findViewById(R.id.time_text)
    }
}