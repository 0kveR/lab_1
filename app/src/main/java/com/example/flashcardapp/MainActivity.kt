package com.example.flashcardapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flashcardapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var ans1: TextView
    private lateinit var ans2: TextView
    private lateinit var ans3: TextView
    private lateinit var questionSide: TextView
    private lateinit var answerSide: TextView
    private lateinit var addBtn: ImageView
    private lateinit var bg: RelativeLayout

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
            addBtn = mainBinding.addBtn
            bg = mainBinding.background
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data

            // TODO: Extract Data
            if (data != null) {
                val questionString = data.getStringExtra("Question")
                val answerString = data.getStringExtra("Answer")

                questionSide.text = questionString
                answerSide.text = answerString
                ans1.visibility = View.INVISIBLE
                ans2.visibility = View.INVISIBLE
                ans3.visibility = View.INVISIBLE
            }
        }

        ans1.setOnClickListener {
            it.background = resources.getDrawable(R.drawable.answer_incorrect)
        }

        ans2.setOnClickListener {
            it.background = resources.getDrawable(R.drawable.answer_incorrect)
        }

        ans3.setOnClickListener {
            it.background = resources.getDrawable(R.drawable.answer_correct)
        }

        questionSide.setOnClickListener { reveal() }

        answerSide.setOnClickListener { conceal() }

        addBtn.setOnClickListener {
            val intent = Intent(this, NewFact::class.java)
            resultLauncher.launch(intent)
        }

        bg.setOnClickListener {
            ans1.background = resources.getDrawable(R.drawable.answer_default)
            ans2.background = resources.getDrawable(R.drawable.answer_default)
            ans3.background = resources.getDrawable(R.drawable.answer_default)
            conceal()
        }
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