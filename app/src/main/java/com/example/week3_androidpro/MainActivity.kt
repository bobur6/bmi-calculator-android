package com.example.week3_androidpro

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.week3_androidpro.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.animation.ObjectAnimator
import android.widget.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastBmi: Float? = null
    private var lastStatus: String? = null
    private var lastAdvice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Toolbar
        setSupportActionBar(binding.toolbar)


        // Restore state
        savedInstanceState?.let {
            lastBmi = it.getFloat("bmi", -1f).takeIf { v -> v > 0 }
            lastStatus = it.getString("status")
            lastAdvice = it.getString("advice")
            lastBmi?.let { bmi -> showResult(bmi, lastStatus ?: "", lastAdvice ?: "") }
        }

        binding.button.setOnClickListener {
            val weight = binding.weight.text.toString().toFloatOrNull()
            val height0 = binding.height.text.toString().toFloatOrNull()
            if (weight == null || height0 == null || weight <= 0 || height0 <= 0) {
                Snackbar.make(binding.root, getString(R.string.error_input), Snackbar.LENGTH_SHORT).show()
                clearResult()
                return@setOnClickListener
            }
            val height = height0 / 100
            val bmi = calculateBmi(weight, height)
            val (status, color) = getBmiStatus(bmi)
            val advice = getBmiAdvice(bmi)
            showResult(bmi, status, advice, color)
            lastBmi = bmi
            lastStatus = status
            lastAdvice = advice
        }

        binding.clearButton.setOnClickListener {
            binding.weight.text.clear()
            binding.height.text.clear()
            clearResult()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastBmi?.let { outState.putFloat("bmi", it) }
        lastStatus?.let { outState.putString("status", it) }
        lastAdvice?.let { outState.putString("advice", it) }
    }

    private fun calculateBmi(weight: Float, height: Float): Float {
        return weight / (height * height)
    }

    private fun getBmiStatus(bmi: Float): Pair<String, Int> {
        return when {
            bmi < 18.5 -> getString(R.string.underweight) to Color.parseColor("#2196F3") // Blue
            bmi < 25 -> getString(R.string.normal_weight) to Color.parseColor("#4CAF50") // Green
            bmi < 30 -> getString(R.string.overweight) to Color.parseColor("#FFC107") // Amber
            else -> getString(R.string.obesity) to Color.parseColor("#F44336") // Red
        }
    }

    private fun getBmiAdvice(bmi: Float): String {
        return when {
            bmi < 18.5 -> getString(R.string.advice_underweight)
            bmi < 25 -> getString(R.string.advice_normal)
            bmi < 30 -> getString(R.string.advice_overweight)
            else -> getString(R.string.advice_obesity)
        }
    }

    private fun showResult(bmi: Float, status: String, advice: String, color: Int = Color.BLACK) {
        val bmiRounded = String.format("%.2f", bmi)
        binding.result.text = getString(R.string.bmi_result, bmiRounded)
        binding.result.setTextColor(color)
        binding.bmiStatusTextView.text = status
        binding.bmiStatusTextView.setTextColor(color)
        binding.adviceTextView.text = advice
        // Анимация
        ObjectAnimator.ofFloat(binding.result, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(binding.bmiStatusTextView, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(binding.adviceTextView, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
    }

    private fun clearResult() {
        binding.result.text = ""
        binding.bmiStatusTextView.text = ""
        binding.adviceTextView.text = ""
    }
}
