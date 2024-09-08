package com.shubham.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shubham.a7minutesworkout.databinding.ActivityExerciseBinding
import com.shubham.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityExerciseBinding

    // TextToSpeech variable
    private var tts: TextToSpeech ?= null
    private var upcomingExercise: String ?= null
    private var isTtsInitialized: Boolean?= false

    // MediaPlayer variable
    private var startPlayer: MediaPlayer ?= null
    private var tickPlayer: MediaPlayer ?= null

    // Variable for Rest CountDown Timer and later on we will initialize it.
    private var restTimer: CountDownTimer ?= null
    // Variable for total rest duration of 10000 milliseconds (10 seconds)
    private val restDuration: Long = 10000
    // Variable for rest timer progress. Initial value the rest progress is set to 0 as we are about to start.
    private var restProgress = 0

    // Variable for Exercise CountDown Timer and later on we will initialize it.
    private var exerciseTimer: CountDownTimer ?= null
    // Variable for total exercise duration of 30000 milliseconds (30 seconds)
    private val exerciseDuration: Long = 30000
    // Variable for exercise timer progress. Initial value the rest progress is set to 0 as we are about to start.
    private var exerciseProgress = 0

    private var restMillisLeft: Long = 0
    private var exerciseMillisLeft: Long = 0

    private var isRestScreenVisible: Boolean = false
    private var isExerciseScreenVisible: Boolean = false

    // Array List of Exercises to be done
    var exerciseList: ArrayList<ExerciseModel> ?= null
    // For tracking current exercise position in ArrayList. We will increment it for each exercise
    var currentExercisePosition = -1

    // Variable for ExerciseStatusAdapter
    private var exerciseStatusAdapter: ExerciseStatusAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.toolbarExercise)

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
        binding.toolbarExercise.setNavigationOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
            showCustomDialogForBackPressed()
        }

        // Setting initial rest timer text as 10 seconds
        binding.tvTimerRest.text = (restDuration/1000).toString()

        // Getting Exercise List from Constants
        exerciseList = Constants.defaultExerciseList()

        // Initialize the Text To Speech
        tts = TextToSpeech(this, this)

        /**
         * Here the sound file is added in to "raw" folder in resources.
         * And played using MediaPlayer. MediaPlayer class can be used to control playback
         * of audio/video files and streams.
         */
        try {
            // For start sound
            val startSoundUri = Uri.parse(
                "android.resource://com.shubham.a7minutesworkout/" + R.raw.app_src_main_res_raw_press_start
            )
            startPlayer = MediaPlayer.create(applicationContext, startSoundUri)
            startPlayer?.isLooping = false   // Sets the player to be looping or non-looping.

            // For tick sound
            val tickSoundUri = Uri.parse(
                "android.resource://com.shubham.a7minutesworkout/" + R.raw.tick
            )
            tickPlayer = MediaPlayer.create(applicationContext, tickSoundUri)
            tickPlayer?.isLooping = true
        }
        catch(e : Exception) {
            e.printStackTrace()
        }

        setUpRestView()

        // setting up the exercise status recycler view
        setUpExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        showCustomDialogForBackPressed()
    }

    private fun showCustomDialogForBackPressed() {

        timerPause()

        if(tickPlayer != null) {
            tickPlayer?.pause()
        }

        val customDialog = Dialog(this)

        // Inflating Custom Dialog using ViewBinding
        // dialog_custom_back_confirmation.xml is the custom layout to inflate
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)

        // Sets whether this dialog is canceled when touched outside the window's bounds.
        // If setting to true, the dialog is set to be cancelable if not already set.
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener {
            // Close the current Activity and dismiss custom dialog
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            // Dismiss custom dialog
            customDialog.dismiss()
            timerResume()
        }
        customDialog.show()     // Show custom dialog
    }

    /**
     * This the TextToSpeech override function
     *
     * Called to signal the completion of the TextToSpeech engine initialization.
     */
    override fun onInit(status: Int) {
        // Checking if the TTS initialization is success or not
        if(status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            // If success, set English as language for tts
            val res = tts?.setLanguage(Locale.ENGLISH)

            // Calling speak() method of TextToSpeech for first exercise
            speakOut("Get ready for ${upcomingExercise!!} in ${restDuration/1000} seconds")

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

    /**
     * Function is used to set the timer for REST.
     */
    private fun setUpRestView() {

        isRestScreenVisible = true
        isExerciseScreenVisible = false

        // Making the visibility of REST Screen Views as VISIBLE and of EXERCISE Screen Views as INVISIBLE
        binding.flRestView.visibility = View.VISIBLE
        binding.tvRest.visibility = View.VISIBLE
        binding.tvUpcomingLabel.visibility = View.VISIBLE
        binding.tvUpcomingExercise.visibility = View.VISIBLE
        binding.flExerciseView.visibility = View.INVISIBLE
        binding.tvExerciseName.visibility = View.INVISIBLE
        binding.ivExercise.visibility = View.INVISIBLE

        if(startPlayer != null) {
            startPlayer?.start()     // Starts playing sound
        }

        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if(restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        // Setting value for upcoming exercise name on REST screen
        // Here as the current position is -1 by default so to selected from the list it should be 0 so we have increased it by +1 (Don't do ++ here!)
        upcomingExercise = exerciseList!![currentExercisePosition+1].name
        binding.tvUpcomingExercise.text = upcomingExercise

        if(isTtsInitialized == true) {
            // Calling speak() method of TextToSpeech
            speakOut("Get ready for ${upcomingExercise!!} in ${restDuration/1000} seconds")
        }

        // This function is used to set the REST progress timer
        setRestProgressBar(restDuration)
    }

    /**
     * Function is used to set the timer for EXERCISE.
     */
    private fun setUpExerciseView() {

        isExerciseScreenVisible = true
        isRestScreenVisible = false

        // Making the visibility of REST Screen Views as INVISIBLE and of EXERCISE Screen Views as VISIBLE
        binding.flRestView.visibility = View.INVISIBLE
        binding.tvRest.visibility = View.INVISIBLE
        binding.tvUpcomingLabel.visibility = View.INVISIBLE
        binding.tvUpcomingExercise.visibility = View.INVISIBLE
        binding.flExerciseView.visibility = View.VISIBLE
        binding.tvExerciseName.visibility = View.VISIBLE
        binding.ivExercise.visibility = View.VISIBLE

        if(startPlayer != null) {
            startPlayer?.start()     // Starts playing sound
        }

        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if(exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        // Setting values for Exercise Name and Image using the exerciseList on EXERCISE Screen
        binding.ivExercise.setImageResource(exerciseList!![currentExercisePosition].image)
        binding.tvExerciseName.text = exerciseList!![currentExercisePosition].name

        speakOut("Start ${exerciseList!![currentExercisePosition].name}")

        // This function is used to set the exercise timer
        setExerciseProgressBar(exerciseDuration)
    }

    /**
     * Function is used to set the progress of timer using the progress
     */
    private fun setRestProgressBar(restDuration: Long) {
        binding.progressBarRest.progress = restProgress  // Sets the current progress to the specified value

        // Here we have started a timer of 10 seconds (10000 milliseconds)
        // and the countdown interval is 1 second (1000 milliseconds)
        restTimer = object: CountDownTimer(
            restDuration, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                if(tickPlayer != null) {
                    tickPlayer?.start()
                }
                restProgress++  // It is increased by 1
                val progress = (restDuration.toInt() / 1000) - restProgress
                binding.tvTimerRest.text = progress.toString()   // Current progress is set to text view in terms of seconds
                restMillisLeft = restDuration - (restProgress.toLong() * 1000)
                binding.progressBarRest.progress = progress    // Indicates progress bar progress
            }

            override fun onFinish() {
                if(tickPlayer != null) {
                    tickPlayer?.pause()
                }
                // When the 10 seconds will complete this will be executed.
                // Incrementing for next exercise
                currentExercisePosition++

                // Setting isSelected boolean for the current exercise as true once the REST timer ends
                exerciseList!![currentExercisePosition].isSelected = true
                // Notifying the adapter about this change so that it can set the background views accordingly
                exerciseStatusAdapter?.notifyDataSetChanged()

                // Setting up the Exercise Progress once the Rest Progress Ends
                setUpExerciseView()
            }
        }.start()
    }

    /**
     * Function is used to set the progress of timer using the progress
     */
    private fun setExerciseProgressBar(exerciseDuration: Long) {
        binding.progressBarExercise.progress = exerciseProgress  // Sets the current progress to the specified value

        // Here we have started a timer of 30 seconds (30000 milliseconds)
        // and the countdown interval is 1 second (1000 milliseconds)
        exerciseTimer = object: CountDownTimer(
            exerciseDuration, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                if(tickPlayer != null) {
                    tickPlayer?.start()
                }
                exerciseProgress++  // It is increased by 1
                val progress = (exerciseDuration.toInt() / 1000) - exerciseProgress
                binding.tvTimerExercise.text = progress.toString()   // Current progress is set to text view in terms of seconds
                exerciseMillisLeft = exerciseDuration - (exerciseProgress.toLong() * 1000)
                binding.progressBarExercise.progress = progress    // Indicates progress bar progress
            }

            override fun onFinish() {
                if(tickPlayer != null) {
                    tickPlayer?.pause()
                }
                // When the 30 seconds will complete this will be executed.

                if(currentExercisePosition < exerciseList?.size!! - 1) {

                    // Setting isSelected boolean for the current exercise as false
                    // and isCompleted boolean for the current exercise as true once the EXERCISE timer ends
                    exerciseList!![currentExercisePosition].isSelected = false
                    exerciseList!![currentExercisePosition].isCompleted = true
                    // Notifying the adapter about this change so that it can set the background views accordingly
                    exerciseStatusAdapter?.notifyDataSetChanged()

                    // Setting up the Rest Progress if more exercises are left
                    setUpRestView()
                }
                else {
//                    Toast.makeText(
//                        this@ExerciseActivity,
//                        "Congratulations! You have finished 7 minutes workout",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    if(startPlayer != null) {
                        startPlayer?.start()     // Starts playing sound
                    }

                    finish()    // Removes the current activity from backstack

                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    /**
     * Function is used to set up the recycler view to UI and assigning the Layout Manager and Adapter Class is attached to it.
     */
    private fun setUpExerciseStatusRecyclerView() {

        // Adapter for recycler view
        exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList!!)   // Since exerciseList is initialized before setUpExerciseStatusRecyclerView() is called, we are sure that it will not be null.
                                                                        // So put !! at end

        // Setting LayoutManager for RecyclerView as LinearLayoutManager with Horizontal Orientation
        binding.exerciseStatusRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Setting adapter to recycler view
        binding.exerciseStatusRV.adapter = exerciseStatusAdapter
    }

    private fun timerPause() {
        if(isRestScreenVisible && restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if(isExerciseScreenVisible && exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
    }

    private fun timerResume() {
        if(isRestScreenVisible && restTimer != null) {
            setRestProgressBar(restMillisLeft)
        }
        if(isExerciseScreenVisible && exerciseTimer != null) {
            setExerciseProgressBar(exerciseMillisLeft)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Cancelling the timers and making restProgress and exerciseProgress as 0
        if(restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        isRestScreenVisible = false
        isExerciseScreenVisible = false

        // we will stop and shutdown the tts which is initialized above
        if(tts != null) {
            tts?.stop()
            tts?.shutdown()
        }

        // Stopping and releasing the player on destroy
        if(startPlayer != null) {
            startPlayer?.stop()
            startPlayer?.release()
        }
        if(tickPlayer != null) {
            tickPlayer?.stop()
            tickPlayer?.release()
        }
    }
}