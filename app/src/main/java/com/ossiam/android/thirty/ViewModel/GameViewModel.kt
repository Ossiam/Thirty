package com.ossiam.android.thirty.ViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ossiam.android.thirty.Model.Game

class GameViewModel(private val state: SavedStateHandle): ViewModel() {

    var game = Game()
    var spinnerIsEnabled: Boolean = true

    fun saveRemovedPointScoring(){

    }
    fun saveState(){
        state["GameState"] = game
    }
    fun getState(): Game? {
        return state["GameState"]
    }

    fun throwDices(): Boolean{
        saveState()
        return game.throwDices()
    }
    fun getNbrThrowsLeft(): Int{
        saveState()
        return game.getNbrThrowsLeft()
    }
    fun setPointScoring(pointScoring: String){
        saveState()
        game.setPointScoring(pointScoring)
    }
    fun getPointScoring(): String {
        saveState()
        return game.getPointScoring()
    }
    fun countScore(diceCombo: ArrayList<Int>){
        saveState()
        game.countScore(diceCombo)
    }
    fun nextRound(): Boolean{
        saveState()
        return game.nextRound()
    }

    fun isGameOver(): Boolean{
        saveState()
        return game.isGameOver()
    }

    fun isRoundOver(): Boolean{
        saveState()
        return game.isRoundOver()
    }

}