package com.shubham.a7minutesworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shubham.a7minutesworkout.databinding.ItemHistoryRowBinding

class ExerciseHistoryAdapter(private val historyDatesList: ArrayList<HistoryEntity>):
            RecyclerView.Adapter<ExerciseHistoryAdapter.ExerciseHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHistoryViewHolder {
        return ExerciseHistoryViewHolder(ItemHistoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ExerciseHistoryViewHolder, position: Int) {
        holder.tvPosition.text = (position+1).toString()
        holder.tvItem.text = historyDatesList[position].date

        if(position%2 == 0) {
            holder.llHistoryItemMain.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int {
        return  historyDatesList.size
    }

    class ExerciseHistoryViewHolder(var binding: ItemHistoryRowBinding): RecyclerView.ViewHolder(binding.root) {
        val llHistoryItemMain = binding.llHistoryItemMain
        val tvPosition = binding.tvPosition
        val tvItem = binding.tvItem
    }
}