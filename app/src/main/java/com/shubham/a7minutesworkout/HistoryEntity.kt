package com.shubham.a7minutesworkout

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise-history-table")
data class HistoryEntity(
    @PrimaryKey
    val date: String
)
