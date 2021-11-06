package com.ossiam.android.thirty.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ossiam.android.thirty.ViewModel.ItemAdapter
import com.ossiam.android.thirty.Model.Score
import com.ossiam.android.thirty.R

class ScoreActivity : AppCompatActivity() {

    private lateinit var restartGameButton: Button
    private lateinit var totalScore: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        //val scoreList = (intent.extras?.getSerializable("scoreList"))
        val scoreList = intent.getParcelableArrayListExtra<Score>("scoreList")
        val recyclerView: RecyclerView = findViewById(R.id.score_list)
        recyclerView.adapter = ItemAdapter(this, scoreList as List<Score>)
        recyclerView.setHasFixedSize(true)

        totalScore = findViewById(R.id.totalScore)
        totalScore.text = "Total score: " + intent.getIntExtra("totalScore", 0)

        restartGameButton = findViewById(R.id.restart_game_btn)
        restartGameButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}