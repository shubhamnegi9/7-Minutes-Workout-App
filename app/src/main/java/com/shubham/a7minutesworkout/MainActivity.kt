package com.shubham.a7minutesworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.shubham.a7minutesworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // create instance of the ActivityMainBinding,
    // as we have only one layout activity_main.xml
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call the static inflate() method included in the generated binding class.
        // This creates an instance of the binding class for the activity to use.
        binding = ActivityMainBinding.inflate(layoutInflater)
        // binding.root returns the root layout, which is activity_main.xml file itself
        // Pass the root view to setContentView() to make it the active view on the screen
        setContentView(binding.root)

//        val flStartBtn: FrameLayout = findViewById(R.id.fl_start)     -> No need to use findViewById() now

        // using the binding variable we can access the layout
        // properties and perform the operations on them as usual
        binding.flStart.setOnClickListener {
            // Using intent to move to ExerciseActivity
            val intent = Intent(this@MainActivity, ExerciseActivity::class.java)
            startActivity(intent)
        }

        binding.flBmi.setOnClickListener {
            val intent = Intent(this@MainActivity, BMIActivity::class.java)
            startActivity(intent)
        }

        binding.flHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

}