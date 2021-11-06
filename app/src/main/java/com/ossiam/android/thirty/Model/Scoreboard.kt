package com.ossiam.android.thirty.Model

class Scoreboard {

    val scoreboardList = mutableListOf<Score>()

    fun insertScore(score: Score){
        scoreboardList.add(score)
    }
    fun getTotalScore(): Int{
        var totalScore = 0
        for(score in scoreboardList){
            totalScore += score.points
        }
        return totalScore
    }
}