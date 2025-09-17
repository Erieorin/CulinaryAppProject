package com.example.culinaryappproject.ui.cooking

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.culinaryappproject.R
import com.example.culinaryappproject.models.Recipe

class StepStageActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe
    private var currentStepIndex = 0

    private lateinit var stepTitle: TextView
    private lateinit var stepDescription: TextView
    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var nextButton: Button

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_stage)

        recipe = intent.getSerializableExtra("recipe", Recipe::class.java)!!

        stepTitle = findViewById(R.id.stepTitle)
        stepDescription = findViewById(R.id.stepDescription)
        timerText = findViewById(R.id.timerText)
        progressBar = findViewById(R.id.progressBar)
        nextButton = findViewById(R.id.nextButton)

        showCurrentStep()

        nextButton.setOnClickListener {
            if (currentStepIndex < recipe.steps.size - 1) {
                currentStepIndex++
                showCurrentStep()
            } else {
                finish()
            }
        }

        findViewById<TextView>(R.id.backStep).setOnClickListener {
            if (currentStepIndex > 0) {
                currentStepIndex--
                showCurrentStep()
            }
        }
    }

    private fun showCurrentStep() {
        val step = recipe.steps[currentStepIndex]
        stepTitle.text = step.title
        stepDescription.text = step.description
        timerText.text = "${step.duration}:00"

        val progress = ((currentStepIndex + 1) * 100) / recipe.steps.size
        progressBar.progress = progress
    }
}