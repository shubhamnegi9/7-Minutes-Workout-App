package com.shubham.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shubham.a7minutesworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val TAG = "HistoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.toolbarHistory)

        // supportActionBar: Retrieve a reference to this activity's ActionBar
        if(supportActionBar != null) {
            /*
            Set whether home should be displayed as an "up" affordance.
            Set this to true if selecting "home" returns up by a single level in your UI rather than
            back to the top level or front page.
            * */
            supportActionBar?.setDisplayHomeAsUpEnabled(true)   // Enables the back button
            supportActionBar?.title = "HISTORY"           // Setting title for ActionBar
        }

        // back button Navigation Listener
        binding.toolbarHistory.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //get the dao through the database in the application class
        val historyDao: HistoryDao = (application as HistoryApplication).db.getHistoryDao()
        // Fetching all the exercise history dates from database
        getExerciseHistoryDatesFormDatabase(historyDao)

        binding.clearBtn.setOnClickListener {
            showClearRecordAlertDialog(historyDao)
        }
    }

    private fun getExerciseHistoryDatesFormDatabase(historyDao: HistoryDao) {

        // launch a coroutine block to execute in background
        lifecycleScope.launch {
            historyDao.getExerciseHistoryDates().collect { historyDatesList ->      // Using collect() method of the flow to emit the data as it changes
                if(historyDatesList.isNotEmpty()) {
                    for (dateObj in historyDatesList) {
                        Log.d(TAG, "getExerciseHistoryDatesFormDatabase: " + dateObj.date)
                    }

                    binding.tvNoDataAvailable.visibility = View.GONE
                    binding.tvHistory.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.clearBtn.visibility = View.VISIBLE

                    val historyDatesArrayList = ArrayList(historyDatesList)
                    val adapter = ExerciseHistoryAdapter(historyDatesArrayList)
                    binding.rvHistory.layoutManager = LinearLayoutManager(this@HistoryActivity)
                    binding.rvHistory.adapter = adapter
                }
                else {
                    binding.tvNoDataAvailable.visibility = View.VISIBLE
                    binding.tvHistory.visibility = View.GONE
                    binding.rvHistory.visibility = View.GONE
                    binding.clearBtn.visibility = View.GONE
                }
            }
        }

    }

    private fun showClearRecordAlertDialog(historyDao: HistoryDao) {
        val clearRecordAlertDialog = AlertDialog.Builder(this)
        clearRecordAlertDialog
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Clear Records")
            .setMessage("Are you sure you want to clear all exercise records?")
            .setCancelable(false)
            .setNegativeButton("No") { it, _ ->
                it.dismiss()
            }
            .setPositiveButton("Yes") { it, _ ->
                lifecycleScope.launch {
                    historyDao.clearAllRecords()
                }
            }
            .create().show()
    }

}