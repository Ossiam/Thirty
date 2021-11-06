package com.ossiam.android.thirty.Model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

private const val STATE_NORMAL = 0
private const val STATE_SELECTED = 1
private const val STATE_MARKED = 2

class Game() : Parcelable{

    private var nbrOfDices = 6
    private var countDice = 0
    private var maxRounds = 11
    private var maxThrows = 2
    private var score: Score = Score()
    var currentRound = 1
    var nbrOfThrows = 0

    var dices = ArrayList<Dice>()
    var scoreboard: Scoreboard = Scoreboard()
    var pointScoringList = mutableListOf("Choose point scoring", "low", "4", "5", "6", "7", "8", "9", "10", "11", "12")

    constructor(parcel: Parcel) : this() {
        nbrOfDices = parcel.readInt()
        countDice = parcel.readInt()
        maxRounds = parcel.readInt()
        maxThrows = parcel.readInt()
        currentRound = parcel.readInt()
        nbrOfThrows = parcel.readInt()
        dices = parcel.readSerializable() as ArrayList<Dice>
    }


    init{
        for(i in 0 until nbrOfDices){ dices.add(Dice()) }
    }

    /**
     * Rolls all the dices, giving them random values
     */
    fun throwDices(): Boolean{
        if(nbrOfThrows < maxThrows){
            nbrOfThrows++
            for(die in dices){ die.roll() }
            return true
        }
        return false
    }

    /**
     * Returns the amount of remaining throws allowed by the player
     */
    fun getNbrThrowsLeft(): Int = maxThrows - nbrOfThrows

    fun setPointScoring(pointScoring: String){ score.pointScoring = pointScoring }

    fun getPointScoring(): String {
        return score.pointScoring
    }

    /**
     * Counts the values of chosen dices and gives points according to the rules of the game
     */
    fun countScore(diceCombo: ArrayList<Int>){
        countDice = 0
        for (dice: Int in diceCombo) { countDice += dice }
        when(score.pointScoring){
            "low" -> if (countDice <= 3) {
                score.points += 3
            }
            else -> {if(score.pointScoring.toInt() == countDice){score.points += countDice} }
        }

        Log.i("Count", countDice.toString())
        Log.i("PointChoice", score.pointScoring)
        Log.i("Points", score.points.toString())
    }

    /**
     * Proceeds to the next round by adding the score to the scoreboard, resets the dices, resets nbrOfthrows
     * and increments the current round
     */
    fun nextRound(): Boolean{
        if(currentRound < maxRounds){
            scoreboard.insertScore(score)
            currentRound++
            resetDices()
            throwDices()
            nbrOfThrows = 0
            score = Score()
            for(dice in dices){
                dice.state = 0
            }
            return true
        }
        return false
    }

    /**
     * Checks if the game is over by checking currentRound against maxRounds
     * If true it adds the score to the scoreboard
     */

    fun isGameOver(): Boolean{
        if(currentRound + 1 == maxRounds){
            scoreboard.insertScore(score)
            return true
        }
        return false
    }

    /**
     * Checks if the round is over by checking if all the dice have been selected and chosen
     */
    fun isRoundOver(): Boolean{
        for(dice in dices){
            if(dice.state == 0 || dice.state == 1){
                return false
            }
        }
        return true
    }

    /**
     * Resets the dice to their default state
     */
    fun resetDices(){
        for(dice in dices){
            dice.state = STATE_NORMAL
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(nbrOfDices)
        parcel.writeInt(countDice)
        parcel.writeInt(maxRounds)
        parcel.writeInt(maxThrows)
        parcel.writeInt(currentRound)
        parcel.writeInt(nbrOfThrows)
        parcel.writeSerializable(dices)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }

}
