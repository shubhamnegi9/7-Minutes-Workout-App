package com.shubham.a7minutesworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shubham.a7minutesworkout.databinding.ItemExerciseStatusBinding

// ExerciseStatusAdapter extends RecyclerView.Adapter
class ExerciseStatusAdapter(private val exerciseList: ArrayList<ExerciseModel>):
            RecyclerView.Adapter<ExerciseStatusAdapter.ExerciseStatusViewHolder>() {

        // Creating our own ViewHolder class inside ExerciseStatusAdapter class which extends RecyclerView.ViewHolder
        inner class ExerciseStatusViewHolder(private val itemBinding: ItemExerciseStatusBinding):   // ItemExerciseStatusBinding is the binding class for item_exercise_status.xml
                RecyclerView.ViewHolder(itemBinding.root) {     // itemBinding.root refers to parent defined in recyclerview_item.xml
                // Holds the TextView that will add each item to
                val tvItem = itemBinding.tvItem
                }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseStatusViewHolder {
        return ExerciseStatusViewHolder(ItemExerciseStatusBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    // Called by RecyclerView to display the data at the specified position.
    // This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
    // This method will be called for each of the item in recyclerview.
    // That means if recycler view have 10 items, then this method will be called 10 times.
    override fun onBindViewHolder(holder: ExerciseStatusViewHolder, position: Int) {
        val exercise = exerciseList[position]

        // Setting the text of each view as the id of that exercise
        holder.tvItem.text = exercise.id.toString()

        // Setting the background and textColor of each view according to
        // whether particular exercise is selected or completed or yet to complete
        when {
            exercise.isSelected -> {
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,      // Using holder.itemView which is RecyclerView's ViewHolder View to get context. ( We cannot pass "this" here as we are not in the context of a class)
                        R.drawable.circular_thin_accent_color_border)
                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
            exercise.isCompleted -> {
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,      // Using holder.itemView which is RecyclerView's ViewHolder View to get context. ( We cannot pass "this" here as we are not in the context of a class)
                        R.drawable.circular_accent_color_background)
                holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else -> {
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,      // Using holder.itemView which is RecyclerView's ViewHolder View to get context. ( We cannot pass "this" here as we are not in the context of a class)
                        R.drawable.circular_gray_color_background
                    )
                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
        }

    }

    // Returns the total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return exerciseList.size
    }

}