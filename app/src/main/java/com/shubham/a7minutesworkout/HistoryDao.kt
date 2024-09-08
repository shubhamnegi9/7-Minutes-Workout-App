package com.shubham.a7minutesworkout

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(historyEntity: HistoryEntity)

    @Query("select * from `exercise-history-table`")
    fun getExerciseHistoryDates(): Flow<List<HistoryEntity>>

    @Query("delete from `exercise-history-table`")      // To clear all records from a table, use the @Query annotation and not @Delete annotation
    suspend fun clearAllRecords()       // make this function as suspend to delete on background thread
}