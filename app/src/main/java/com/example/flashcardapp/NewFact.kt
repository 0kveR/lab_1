package com.example.flashcardapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.flashcardapp.databinding.ActivityNewFactBinding

class NewFact : AppCompatActivity() {
    private lateinit var newFactBinding: ActivityNewFactBinding
    private lateinit var cancel: ImageView
    private lateinit var save: ImageView
    private lateinit var question: EditText
    private lateinit var answer: EditText
    private lateinit var incorrectAnswer1: EditText
    private lateinit var incorrectAnswer2: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newFactBinding = ActivityNewFactBinding.inflate(layoutInflater)
        setContentView(newFactBinding.root)

        newFactBinding.let {
            cancel = newFactBinding.cancelBtn
            save = newFactBinding.saveBtn
            question = newFactBinding.addQuestion
            answer = newFactBinding.addAnswer
            incorrectAnswer1 = newFactBinding.wrongAnswer
            incorrectAnswer2 = newFactBinding.wrongAnswer2
        }

        val mode = intent.getStringExtra("EditMode")

        if (mode == "true") {
            question.setText(intent.getStringExtra("Question"))
            answer.setText(intent.getStringExtra("CorrectAnswer"))
            newFactBinding.header.text = "Edit Flashcard"
            incorrectAnswer1.setText(intent.getStringExtra("IncorrectAnswer"))
            incorrectAnswer2.setText(intent.getStringExtra("IncorrectAnswer2"))
        }

        cancel.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            if (question.text.toString().isNotBlank() && answer.text.toString().isNotBlank()) {
                val data = Intent()
                data.putExtra("Question", question.text.toString())
                data.putExtra("Answer", answer.text.toString())

                val multiChoice = multiChoice()
                when (multiChoice) {
                    0 -> data.putExtra("Multi", "0")
                    1 -> {
                        data.putExtra("Multi", "1")
                        data.putExtra("IncorrectAnswer1", incorrectAnswer1.text.toString())
                    }
                    2 -> {
                        data.putExtra("Multi", "1")
                        data.putExtra("IncorrectAnswer1", incorrectAnswer2.text.toString())
                    }
                    3 -> {
                        data.putExtra("Multi", "3")
                        data.putExtra("IncorrectAnswer1", incorrectAnswer1.text.toString())
                        data.putExtra("IncorrectAnswer2", incorrectAnswer2.text.toString())
                    }
                }

                setResult(RESULT_OK, data)
                finish()
            } else {
                Toast.makeText(applicationContext, "Question and Answer are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun multiChoice(): Int {
        if ((incorrectAnswer1.text.toString().isNotBlank() && incorrectAnswer2.text.toString().isNotBlank())) {
            return 3
        } else if (incorrectAnswer1.text.toString().isNotBlank() || incorrectAnswer2.text.toString().isNotBlank()) {
            if (incorrectAnswer1.text.toString().isNotBlank()) {
                return 1
            } else {
                return 2
            }
        } else {
            return 0
        }
    }
}