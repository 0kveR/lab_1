package com.example.flashcardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.flashcardapp.databinding.ActivityNewFactBinding

class NewFact : AppCompatActivity() {
    private lateinit var newFactBinding: ActivityNewFactBinding
    private lateinit var cancel: ImageView
    private lateinit var save: ImageView
    private lateinit var question: TextView
    private lateinit var answer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newFactBinding = ActivityNewFactBinding.inflate(layoutInflater)
        setContentView(newFactBinding.root)

        newFactBinding.let {
            cancel = newFactBinding.cancelBtn
            save = newFactBinding.saveBtn
            question = newFactBinding.addQuestion
            answer = newFactBinding.addAnswer
        }

        cancel.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            if (question.text.toString().isNotBlank() && answer.text.toString().isNotBlank()) {
                val data = Intent()
                data.putExtra("Question", question.text.toString())
                data.putExtra("Answer", answer.text.toString())
                setResult(RESULT_OK, data)
                finish()
            }
        }
    }
}