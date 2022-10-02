package com.example.flashcardapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flashcardapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var ans1: TextView
    private lateinit var ans2: TextView
    private lateinit var ans3: TextView
    private lateinit var questionSide: TextView
    private lateinit var answerSide: TextView
    private lateinit var addFact: ImageView
    private lateinit var bg: RelativeLayout
    private lateinit var editFact: ImageView
    var correctAnswer = 3

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        var switch = false

        mainBinding.let {
            ans1 = mainBinding.ans1
            ans2 = mainBinding.ans2
            ans3 = mainBinding.ans3
            questionSide = mainBinding.questionSide
            answerSide = mainBinding.answerSide
            addFact = mainBinding.addBtn
            bg = mainBinding.background
            editFact = mainBinding.editBtn
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data

            if (data != null) {
                Snackbar.make(bg, "Card created successfully", Snackbar.LENGTH_SHORT).show()
                val questionString = data.getStringExtra("Question")
                val answerString = data.getStringExtra("Answer")
                val multi = data.getStringExtra("Multi")

                questionSide.text = questionString
                answerSide.text = answerString

                when (multi) {
                    "0" -> {
                        resetColors()
                        ans1.visibility = View.INVISIBLE
                        ans2.visibility = View.INVISIBLE
                        ans3.visibility = View.INVISIBLE
                        ans1.text = ""
                        ans2.text = ""
                        ans3.text = ""
                    }
                    "1" -> {
                        resetColors()
                        ans1.visibility = View.VISIBLE
                        ans2.visibility = View.VISIBLE
                        ans3.visibility = View.INVISIBLE
                        correctAnswer = (1..2).random()
                        if (correctAnswer == 1) {
                            ans1.text = data.getStringExtra("Answer")
                            ans2.text = data.getStringExtra("IncorrectAnswer1")
                        } else {
                            ans1.text = data.getStringExtra("IncorrectAnswer1")
                            ans2.text = data.getStringExtra("Answer")
                        }
                        ans3.text = ""
                    }
                    "3" -> {
                        resetColors()
                        ans1.visibility = View.VISIBLE
                        ans2.visibility = View.VISIBLE
                        ans3.visibility = View.VISIBLE
                        correctAnswer = (1..3).random()
                        if (correctAnswer == 1) {
                            ans1.text = data.getStringExtra("Answer")
                            ans2.text = data.getStringExtra("IncorrectAnswer1")
                            ans3.text = data.getStringExtra("IncorrectAnswer2")
                        } else if (correctAnswer == 2) {
                            ans1.text = data.getStringExtra("IncorrectAnswer1")
                            ans2.text = data.getStringExtra("Answer")
                            ans3.text = data.getStringExtra("IncorrectAnswer2")
                        } else {
                            ans1.text = data.getStringExtra("IncorrectAnswer1")
                            ans2.text = data.getStringExtra("IncorrectAnswer2")
                            ans3.text = data.getStringExtra("Answer")
                        }
                    }
                }
            } else {
                Snackbar.make(bg, "Card discarded ;)", Snackbar.LENGTH_SHORT).show()
            }
        }

        ans1.setOnClickListener {
            if (correctAnswer == 1) {
                it.background = resources.getDrawable(R.drawable.answer_correct)
            } else {
                it.background = resources.getDrawable(R.drawable.answer_incorrect)
            }
        }

        ans2.setOnClickListener {
            if (correctAnswer == 2) {
                it.background = resources.getDrawable(R.drawable.answer_correct)
            } else {
                it.background = resources.getDrawable(R.drawable.answer_incorrect)
            }
        }

        ans3.setOnClickListener {
            if (correctAnswer == 3) {
                it.background = resources.getDrawable(R.drawable.answer_correct)
            } else {
                it.background = resources.getDrawable(R.drawable.answer_incorrect)
            }
        }

        questionSide.setOnClickListener { reveal() }

        answerSide.setOnClickListener { conceal() }

        addFact.setOnClickListener {
            val intent = Intent(this, NewFact::class.java)
            intent.putExtra("EditMode", "false")
            resultLauncher.launch(intent)
        }

        editFact.setOnClickListener {
            val intent = Intent(this, NewFact::class.java)
            intent.putExtra("EditMode", "true")
            intent.putExtra("Question", questionSide.text.toString())
            intent.putExtra("CorrectAnswer", answerSide.text.toString())
            if (correctAnswer == 1) {
                intent.putExtra("IncorrectAnswer", ans2.text.toString())
                intent.putExtra("IncorrectAnswer2", ans3.text.toString())
            } else if (correctAnswer == 2) {
                intent.putExtra("IncorrectAnswer", ans1.text.toString())
                intent.putExtra("IncorrectAnswer2", ans3.text.toString())
            } else {
                intent.putExtra("IncorrectAnswer", ans1.text.toString())
                intent.putExtra("IncorrectAnswer2", ans2.text.toString())
            }
            resultLauncher.launch(intent)
        }

        bg.setOnClickListener {
            resetColors()
            conceal()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun resetColors() {
        ans1.background = resources.getDrawable(R.drawable.answer_default)
        ans2.background = resources.getDrawable(R.drawable.answer_default)
        ans3.background = resources.getDrawable(R.drawable.answer_default)
    }

    private fun reveal() {
        questionSide.visibility = View.INVISIBLE
        answerSide.visibility = View.VISIBLE
    }

    private fun conceal() {
        answerSide.visibility = View.INVISIBLE
        questionSide.visibility = View.VISIBLE
    }
}