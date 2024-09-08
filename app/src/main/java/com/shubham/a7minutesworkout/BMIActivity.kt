package com.shubham.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.shubham.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNITS_VIEW"
        private const val US_UNITS_VIEW = "US_UNITS_VIEW"
    }

    private var binding: ActivityBmiBinding ?= null
    private var currentVisibleView: String = METRIC_UNITS_VIEW  // A variable to hold a value to make a selected view visible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding?.toolbarBMI)

        // supportActionBar: Retrieve a reference to this activity's ActionBar
        if(supportActionBar != null) {
            /*
            Set whether home should be displayed as an "up" affordance.
            Set this to true if selecting "home" returns up by a single level in your UI rather than
            back to the top level or front page.
            * */
            supportActionBar?.setDisplayHomeAsUpEnabled(true)   // Enables the back button
            supportActionBar?.title = "Calculate BMI"           // Setting title for ActionBar
        }

        // back button Navigation Listener
        binding?.toolbarBMI?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //  When the activity is launched make METRIC UNITS VIEW visible
        makeVisibleMetricUnitsView()

        // Button will calculate the input values in Metric Units
        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }

        // Adding a check change listener to the radio group and according to the radio button.
        // Radio Group change listener is set to the radio group which is added in XML.
        // we use _ for the first value because we don't need it
        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            }
            else {
                makeVisibleUSUnitsView()
            }
        }

    }

    /**
     * Function is used to validate the input values for METRIC UNITS.
     */
    private fun validateMetricUnits(): Boolean {
        var isValid = true
        // If either Weight or Height EditText remains empty, then no more valid
        if(binding?.etMetricWeight?.text.toString().isEmpty() || binding?.etMetricHeight?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    /**
     * Function is used to validate the input values for US UNITS.
     */
    private fun valdateUSUnits(): Boolean {
        var isValid = true
        if(binding?.etUSWeight?.text.toString().isEmpty() ||
                binding?.etUSFeet?.text.toString().isEmpty() ||
                binding?.etUSInch?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    private fun calculateUnits() {
        if(currentVisibleView == METRIC_UNITS_VIEW) {
            if(validateMetricUnits()) {
                val weightMetricValue = binding?.etMetricWeight?.text.toString().toFloat()          // weight in kg
                val heightMetricValue = binding?.etMetricHeight?.text.toString().toFloat() / 100    // height in meters
                // BMI value is calculated in METRIC UNITS using the height and weight value.
                val bmi = weightMetricValue / (heightMetricValue * heightMetricValue)
                displayBMIResults(bmi)
            }
            else {
                Toast.makeText(this@BMIActivity, "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            if(valdateUSUnits()) {
                val weightUSValue = binding?.etUSWeight?.text.toString().toFloat()      // weight in pounds
                val feetUSValue = binding?.etUSFeet?.text.toString().toFloat()          // height in feet
                val inchUSValue = binding?.etUSInch?.text.toString().toFloat()          // height in inch
                // Here the Height Feet and Inch values are merged and multiplied by 12 for converting it to inches
                val heightUSValue = feetUSValue * 12 + inchUSValue
                // BMI value is calculated in US UNITS using the height and weight value.
                val bmi = 703 * (weightUSValue / (heightUSValue * heightUSValue))
                displayBMIResults(bmi)
            }
            else {
                Toast.makeText(this@BMIActivity, "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Function is used to display the result of METRIC UNITS.
     */
    private fun displayBMIResults(bmi: Float) {

        val bmiLabel: String
        val bmiDescription: String

        /*
        * compareTo() : Compares this value with the specified value for order.
        *               Returns zero if this value is equal to the specified other value,
        *               a negative number if it's less than other, or a positive number if it's greater than other.
        * */
        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        //Use to set the result layout visible
        binding?.llBmiResult?.visibility = View.VISIBLE

        // This is used to round the result value to 2 decimal values after "."
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        // Setting required results
        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDesc?.text = bmiDescription
    }

    /**
     * Function is used to make the METRIC UNITS VIEW visible and hide the US UNITS VIEW.
     */
    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.weightMetricTextInput?.visibility = View.VISIBLE
        binding?.heightMetricTextInput?.visibility = View.VISIBLE
        binding?.weightUSTextInput?.visibility = View.INVISIBLE
        binding?.feetUSTextInput?.visibility = View.INVISIBLE
        binding?.inchUSTextInput?.visibility = View.INVISIBLE

        binding?.etMetricWeight?.text?.clear()  // weight value is cleared if it is added.
        binding?.etMetricHeight?.text?.clear()  // height value is cleared if it is added.

        binding?.llBmiResult?.visibility = View.INVISIBLE   // making BMI results as INVISIBLE
    }

    /**
     * Function is used to make the US UNITS VIEW visible and hide the METRIC UNITS VIEW.
     */
    private fun makeVisibleUSUnitsView() {
        currentVisibleView = US_UNITS_VIEW
        binding?.weightMetricTextInput?.visibility = View.INVISIBLE
        binding?.heightMetricTextInput?.visibility = View.INVISIBLE
        binding?.weightUSTextInput?.visibility = View.VISIBLE
        binding?.feetUSTextInput?.visibility = View.VISIBLE
        binding?.inchUSTextInput?.visibility = View.VISIBLE

        binding?.etUSWeight?.text?.clear()  // weight value is cleared.
        binding?.etUSFeet?.text?.clear()    // height feet value is cleared.
        binding?.etUSInch?.text?.clear()    // height inch is cleared.

        binding?.llBmiResult?.visibility = View.INVISIBLE   // making BMI results as INVISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}