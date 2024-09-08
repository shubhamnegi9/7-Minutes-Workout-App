package com.shubham.a7minutesworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.shubham.a7minutesworkout.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var binding: ActivityFinishBinding
    // TextToSpeech variable
    private var tts: TextToSpeech?= null
    private val TAG = "FinishActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.toolbarFinish)

        // supportActionBar: Retrieve a reference to this activity's ActionBar
        if(supportActionBar != null) {
            /*
            Set whether home should be displayed as an "up" affordance.
            Set this to true if selecting "home" returns up by a single level in your UI rather than
            back to the top level or front page.
            * */
            supportActionBar?.setDisplayHomeAsUpEnabled(true)   // Enables the back button
        }

        // back button Navigation Listener
        binding.toolbarFinish.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Initialize the Text To Speech
        tts = TextToSpeech(this, this)

        binding.btnFinish.setOnClickListener {
            finish()    // Removes the current activity from backstack
        }

        //get the dao through the database in the application class
        val historyDao: HistoryDao = (application as HistoryApplication).db.getHistoryDao()
        // Adding current date time to database once the Finish Activity is called
        addDateToDatabase(historyDao)
    }

    /**
     * Function is used to insert the current system date in the RoomDB database.
     */
    private fun addDateToDatabase(historyDao: HistoryDao) {

        val cal = Calendar.getInstance()    // Calendars Current Instance
        val dateTime = cal.time             // Current Date and Time of the system.
        Log.d(TAG, "dateTime: " + dateTime)

        /**
         * Here we have taken an instance of Date Formatter as it will format our
         * selected date in the format which we pass it as an parameter and Locale.
         * Here we have passed the format as dd MMM yyyy HH:mm:ss.
         *
         * The Locale : Gets the current value of the default locale for this instance
         * of the Java Virtual Machine.
         */
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())  // Date Formatter
        val date = sdf.format(dateTime)      // dateTime is formatted in the given format
        Log.d(TAG, "date: " + date)

        // launch a coroutine block to execute in background
        lifecycleScope.launch {
            historyDao.insert(HistoryEntity(date))
            Log.d(TAG, "DATE ADDED TO DATABASE")
        }
    }

    override fun onInit(status: Int) {
        // Checking if the TTS initialization is success or not
        if(status == TextToSpeech.SUCCESS) {
            // If success, set English as language for tts
            val res = tts?.setLanguage(Locale.ENGLISH)

            // Calling speak() method of TextToSpeech on finishing
            speakOut("Congratulations! You have finished 7 minutes workout")

            if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "onInit: Language Not Supported")
            }
        }
        else {
            Log.e("TTS", "onInit: Initialization Failed")
        }
    }

    /**
     * Function is used to speak the text what we pass to it.
     */
    private fun speakOut(text: String) {
        /*
        * speak() - Speaks the text using the specified queuing strategy and speech parameters
        *
        * TextToSpeech.QUEUE_FLUSH -
        * Queue mode where all entries in the playback queue (media to be played and text to be synthesized)
        * are dropped and replaced by the new entry.
        *
        * TextToSpeech.QUEUE_ADD -
        * Queue mode where the new entry is added at the end of the playback queue.
        *
        * */
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

    }
}

