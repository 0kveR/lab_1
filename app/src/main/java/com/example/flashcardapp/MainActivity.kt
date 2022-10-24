package com.example.flashcardapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flashcardapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var choice1: TextView
    private lateinit var choice2: TextView
    private lateinit var choice3: TextView
    private lateinit var questionSide: TextView
    private lateinit var answerSide: TextView
    private lateinit var addFact: ImageView
    private lateinit var editFact: ImageView
    private lateinit var prev: ImageView
    private lateinit var next: ImageView
    private lateinit var trash: ImageView
    private lateinit var bg: RelativeLayout
    private var correctAnswer: Int = randInt(1, 3)
    private var currentCard: Int = 0

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        flashcardDatabase = FlashcardDatabase(this)
        updateList()

        if (allFlashcards.size == 0) {
            flashcardDatabase.initFirstCard()
            updateList()
        }

        mainBinding.let {
            choice1 = mainBinding.ans1
            choice2 = mainBinding.ans2
            choice3 = mainBinding.ans3
            questionSide = mainBinding.questionSide
            answerSide = mainBinding.answerSide
            addFact = mainBinding.addBtn
            editFact = mainBinding.editBtn
            prev = mainBinding.prevBtn
            next = mainBinding.nextBtn
            trash = mainBinding.trash
            bg = mainBinding.background
        }

        cardSetup(currentCard)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data

                if (data != null) {
                    val questionString = data.getStringExtra("Question")
                    val answerString = data.getStringExtra("Answer")
                    val multi = data.getStringExtra("Multi")
                    val mode = data.getStringExtra("EditMode")

                    if (questionString == null || answerString == null) {
                        return@registerForActivityResult
                    }

                    var incorrectAnswer1: String? = null
                    var incorrectAnswer2: String? = null

                    resetColors()
                    when (multi) {
                        "0" -> {
                            Log.i("ResultLauncher", "No multi-choice answers")
                        }
                        "1" -> {
                            if (data.getStringExtra("IncorrectAnswer1") != null) {
                                incorrectAnswer1 = data.getStringExtra("IncorrectAnswer1")
                            }
                        }
                        "3" -> {
                            if (data.getStringExtra("IncorrectAnswer1") != null && data.getStringExtra(
                                    "IncorrectAnswer2"
                                ) != null
                            ) {
                                incorrectAnswer1 = data.getStringExtra("IncorrectAnswer1")
                                incorrectAnswer2 = data.getStringExtra("IncorrectAnswer2")
                            }
                        }
                    }

                    if (mode == "true") {
                        flashcardDatabase.updateCard(Flashcard(questionString, answerString, incorrectAnswer1, incorrectAnswer2))
                        updateList()
                        Snackbar.make(bg, "Card updated successfully", Snackbar.LENGTH_SHORT).show()
                    } else {
                        flashcardDatabase.insertCard(Flashcard(questionString, answerString, incorrectAnswer1, incorrectAnswer2))
                        updateList()
                        currentCard = allFlashcards.size - 1
                        Snackbar.make(bg, "Card created successfully", Snackbar.LENGTH_SHORT).show()
                    }
                    cardSetup(currentCard)
                } else {
                    Snackbar.make(bg, "Card discarded ;)", Snackbar.LENGTH_SHORT).show()
                }
            }

        choice1.setOnClickListener {
            if (correctAnswer == 1) {
                it.background = resources.getDrawable(R.drawable.answer_correct)
            } else {
                it.background = resources.getDrawable(R.drawable.answer_incorrect)
            }
        }

        choice2.setOnClickListener {
            if (correctAnswer == 2) {
                it.background = resources.getDrawable(R.drawable.answer_correct)
            } else {
                it.background = resources.getDrawable(R.drawable.answer_incorrect)
            }
        }

        choice3.setOnClickListener {
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
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        editFact.setOnClickListener {
            val intent = Intent(this, NewFact::class.java)
            intent.putExtra("EditMode", "true")
            intent.putExtra("Question", questionSide.text.toString())
            intent.putExtra("CorrectAnswer", answerSide.text.toString())
            when (correctAnswer) {
                1 -> {
                    intent.putExtra("IncorrectAnswer", choice2.text.toString())
                    intent.putExtra("IncorrectAnswer2", choice3.text.toString())
                }
                2 -> {
                    intent.putExtra("IncorrectAnswer", choice1.text.toString())
                    intent.putExtra("IncorrectAnswer2", choice3.text.toString())
                }
                else -> {
                    intent.putExtra("IncorrectAnswer", choice1.text.toString())
                    intent.putExtra("IncorrectAnswer2", choice2.text.toString())
                }
            }
            resultLauncher.launch(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        prev.setOnClickListener {
            if (allFlashcards.size == 1) {
                return@setOnClickListener
            }

            resetColors()
            if (currentCard == 0) {
                currentCard = allFlashcards.size - 1
                cardSetup(currentCard)
            } else {
                currentCard--
                cardSetup(currentCard)
            }
        }

        next.setOnClickListener {
            if (allFlashcards.size == 1) {
                return@setOnClickListener
            }

            val leftOut = AnimationUtils.loadAnimation(this, R.anim.left_out)
            val rightIn = AnimationUtils.loadAnimation(this, R.anim.right_in)

            leftOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    choice1.startAnimation(leftOut)
                    choice2.startAnimation(leftOut)
                    choice3.startAnimation(leftOut)
                }

                override fun onAnimationEnd(p0: Animation?) {
                    resetColors()
                    if (currentCard == allFlashcards.size - 1) {
                        currentCard = 0
                        cardSetup(currentCard)
                    } else {
                        currentCard++
                        cardSetup(currentCard)
                    }

                    if (answerSide.visibility == View.VISIBLE) {
                        answerSide.visibility = View.INVISIBLE
                        questionSide.visibility = View.VISIBLE
                    }

                    questionSide.startAnimation(rightIn)
                    choice1.startAnimation(rightIn)
                    choice2.startAnimation(rightIn)
                    choice3.startAnimation(rightIn)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                    //
                }
            })

            if (answerSide.visibility == View.VISIBLE) {
                answerSide.startAnimation(leftOut)
            } else {
                questionSide.startAnimation(leftOut)
            }
        }

        trash.setOnClickListener {
            resetColors()
            flashcardDatabase.deleteCard(allFlashcards[currentCard].question)
            updateList()

            if (currentCard == 0) {
                currentCard = allFlashcards.size
            }

            currentCard--
            cardSetup(currentCard)
        }

        bg.setOnClickListener {
            resetColors()
            conceal()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun resetColors() {
        choice1.background = resources.getDrawable(R.drawable.answer_default)
        choice2.background = resources.getDrawable(R.drawable.answer_default)
        choice3.background = resources.getDrawable(R.drawable.answer_default)
    }

    private fun reveal() {
        val cx = answerSide.width / 2
        val cy = answerSide.height / 2
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(answerSide, cx, cy, 0f, finalRadius)
        questionSide.visibility = View.INVISIBLE
        answerSide.visibility = View.VISIBLE
        anim.duration = 500
        anim.start()
    }

    private fun conceal() {
        answerSide.visibility = View.INVISIBLE
        questionSide.visibility = View.VISIBLE
    }

    private fun makeChoicesVisible() {
        choice1.visibility = View.VISIBLE
        choice2.visibility = View.VISIBLE
        choice3.visibility = View.VISIBLE
    }

    private fun randInt(low: Int, high: Int): Int {
        return (low..high).random()
    }

    private fun updateList() {
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()
    }

    fun cardSetup(cardNumber: Int) {
        questionSide.text = allFlashcards[cardNumber].question
        answerSide.text = allFlashcards[cardNumber].answer

        if (allFlashcards[cardNumber].wrongAnswer1 != null && allFlashcards[cardNumber].wrongAnswer2 != null) {
            correctAnswer = randInt(1, 3)
            makeChoicesVisible()

            when (correctAnswer) {
                1 -> {
                    choice1.text = allFlashcards[cardNumber].answer
                    choice2.text = allFlashcards[cardNumber].wrongAnswer1
                    choice3.text = allFlashcards[cardNumber].wrongAnswer2
                }
                2 -> {
                    choice1.text = allFlashcards[cardNumber].wrongAnswer1
                    choice2.text = allFlashcards[cardNumber].answer
                    choice3.text = allFlashcards[cardNumber].wrongAnswer2
                }
                else -> {
                    choice1.text = allFlashcards[cardNumber].wrongAnswer1
                    choice2.text = allFlashcards[cardNumber].wrongAnswer2
                    choice3.text = allFlashcards[cardNumber].answer
                }
            }
        } else if (allFlashcards[cardNumber].wrongAnswer1 != null) {
            correctAnswer = randInt(1, 2)
            makeChoicesVisible()
            choice2.visibility = View.INVISIBLE
            choice2.text = ""

            if (correctAnswer == 1) {
                choice1.text = allFlashcards[cardNumber].answer
                choice3.text = allFlashcards[cardNumber].wrongAnswer1
            } else {
                choice1.text = allFlashcards[cardNumber].wrongAnswer1
                choice3.text = allFlashcards[cardNumber].answer
            }
        } else {
            correctAnswer = 1
            choice1.visibility = View.INVISIBLE
            choice2.visibility = View.INVISIBLE
            choice3.visibility = View.INVISIBLE
            choice1.text = ""
            choice2.text = ""
            choice3.text = ""
        }
    }
}