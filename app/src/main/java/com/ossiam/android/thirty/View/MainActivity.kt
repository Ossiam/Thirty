package com.ossiam.android.thirty.View

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.ossiam.android.thirty.R
import com.ossiam.android.thirty.ViewModel.GameViewModel
import com.ossiam.android.thirty.databinding.ActivityMainBinding


private const val STATE_NORMAL = 0
private const val STATE_SELECTED = 1
private const val STATE_MARKED = 2

private const val CURRENT_ROUND = "CURRENT_ROUND"
private const val NBR_THROWS = "NBR_THROWS"
private const val SPINNER_ENABLED = "SPINNER_ENABLED"
private const val GAME_STATE = "GAME_STATE"

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)

    private lateinit var diceViewList: ArrayList<ImageView>
    private lateinit var diceCombo: ArrayList<Int>
    private lateinit var throwBtn: Button
    private lateinit var endRoundBtn: Button
    private lateinit var chooseDiceBtn: Button
    private lateinit var throwsLeft: TextView
    private lateinit var currentRound: TextView

    private lateinit var spinner: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var viewModel: GameViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        if(savedInstanceState != null){
            viewModel.game.currentRound = savedInstanceState.getInt(CURRENT_ROUND, 0)
            viewModel.game.nbrOfThrows = savedInstanceState.getInt(NBR_THROWS, 0)
            viewModel.spinnerIsEnabled = savedInstanceState.getBoolean(SPINNER_ENABLED, true)
            viewModel.game = savedInstanceState.getParcelable(GAME_STATE)!!
        }

        diceListenersSetup(binding)
        setupSpinner(binding)
        setupDiceListeners()
        setupThrowBtn(binding)
        setupChooseBtn(binding)
        setupEndRoundBtn(binding)
        updateDiceImages()
        for(i in 0 until diceViewList.size){
            diceBorderPlacement(i)
            diceInvisibility(i)
            Log.i("DiceValue", viewModel.game.dices[i].value.toString())
        }
        updateViews(binding)
    }

    private fun updateViews(binding: ActivityMainBinding){
        throwsLeft = binding.throwsLeft
        currentRound = binding.currentRound
        throwsLeft.text = getString(R.string.throws_left, viewModel.getNbrThrowsLeft())
        currentRound.text = getString(R.string.current_round, viewModel.game.currentRound)
        spinner.isEnabled = viewModel.spinnerIsEnabled
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupDiceListeners() {
        /**
         * Sets listeners for clicking on the dice images
         * Places borders when a die is selected
         */
        for(i in 0 until diceViewList.size){
            diceViewList[i].setOnClickListener {
                val dice = viewModel.game.dices[i]
                if (dice.state == STATE_SELECTED) dice.state = STATE_NORMAL
                else dice.state = STATE_SELECTED
                diceBorderPlacement(i)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupThrowBtn(binding: ActivityMainBinding){
        /**
         * Throws all unselected dice and updates the view
         */
        throwBtn = binding.throwDiceBtn
        throwBtn.setOnClickListener{
            if(viewModel.throwDices()){
                updateDiceImages()
                updateViews(binding)
            }else{
                Snackbar.make(it, "Out of throws!", Snackbar.LENGTH_SHORT).show()
            }
            Log.i("THROWS", viewModel.game.nbrOfThrows.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupChooseBtn(binding: ActivityMainBinding){
        /**
         * Marks selected dice and sends their value to the game model
         * Marked dice becomes invisible and unselectable until next round
         * The spinner is disabled to prevent further changes from the user
         */
        diceCombo = ArrayList()
        chooseDiceBtn = binding.chooseDiceBtn
        chooseDiceBtn.setOnClickListener{
            Log.i("PointScore", viewModel.getPointScoring())
            if(viewModel.getPointScoring() == "" || spinner.selectedItem.toString() == "Choose point scoring"){
                Snackbar.make(it, "You haven't picked any point scoring", Snackbar.LENGTH_SHORT).show()
            }else if(viewModel.isRoundOver()){
                Snackbar.make(it, "End the round", Snackbar.LENGTH_SHORT).show()
            } else if(!viewModel.game.dices.any{it.state == 1}){ //Makes sure that atleast 1 die is selected
                Snackbar.make(it, "You haven't chosen any dice!", Snackbar.LENGTH_SHORT).show()
            }
            else{
                viewModel.spinnerIsEnabled = false
                spinner.isEnabled = viewModel.spinnerIsEnabled
                diceCombo.clear()
                for((index, dice) in viewModel.game.dices.withIndex()){
                    if(dice.state == STATE_SELECTED){
                        dice.state = STATE_MARKED
                        diceBorderPlacement(index)
                        diceInvisibility(index)
                        diceCombo.add(viewModel.game.dices[index].value)
                        //Log.i("DiceValue $index", game.getDice(index).getValue().toString())
                        Log.i("diceCombo", diceCombo.toString())
                    }
                }
                viewModel.countScore(diceCombo)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupEndRoundBtn(binding: ActivityMainBinding){
        /**
         * Ends current round, removes selected item from the spinner
         * and enables the spinner
         * If current round is the last round the user is directed to ScoreActivity
         */
        endRoundBtn = binding.endRoundBtn
        endRoundBtn.setOnClickListener{
            Log.i("CurrentRound", viewModel.game.currentRound.toString())
            if(!viewModel.isRoundOver()){
                Snackbar.make(it, "You haven't picked all the dices!", Snackbar.LENGTH_SHORT).show()
            }else if(viewModel.isGameOver()){ //viewModel.game.currentRound + 1 == 2
                val intent = Intent(this, ScoreActivity::class.java)
                //intent.putExtra("scoreList", viewModel.game.scoreboard.scoreboardList as ArrayList)
                intent.putParcelableArrayListExtra("scoreList", viewModel.game.scoreboard.scoreboardList as ArrayList)
                intent.putExtra("totalScore", viewModel.game.scoreboard.getTotalScore())
                startActivity(intent)
                finish()
            }
            else{
                viewModel.spinnerIsEnabled = true
                spinner.isEnabled = viewModel.spinnerIsEnabled
                viewModel.game.pointScoringList.remove(spinner.selectedItem.toString())
                spinner.setSelection(0)
                for(dice in diceViewList){
                    dice.visibility = VISIBLE
                }
                viewModel.nextRound()
                updateDiceImages()
                updateViews(binding)
            }
        }
    }

    /**
     * Setups the spinner
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupSpinner(binding: ActivityMainBinding) {
        spinner = binding.spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewModel.game.pointScoringList).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.setSelection(0)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) { //Nullcheck for rotation, otherwise it crashes
                    if(parent.selectedItem != "Choose point scoring"){
                        viewModel.setPointScoring(parent.selectedItem as String)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    /**
     * Adds the die views to a list for easier application of listeners
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun diceListenersSetup(binding: ActivityMainBinding){
        diceViewList = ArrayList()
        diceViewList.add(binding.dice1)
        diceViewList.add(binding.dice2)
        diceViewList.add(binding.dice3)
        diceViewList.add(binding.dice4)
        diceViewList.add(binding.dice5)
        diceViewList.add(binding.dice6)
    }

    /**
     * Places a border around a die view if they are selected
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun diceBorderPlacement(diceIndex: Int) {
        if(viewModel.game.dices[diceIndex].state == STATE_SELECTED){
            diceViewList[diceIndex].foreground = this.getDrawable(R.drawable.border)
        }else{
            diceViewList[diceIndex].foreground = this.getDrawable(R.drawable.transparent)
        }
    }

    /**
     * Turns the die view invisible is they are marked
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun diceInvisibility(diceIndex: Int){
        if(viewModel.game.dices[diceIndex].state == STATE_MARKED){
            diceViewList[diceIndex].visibility = INVISIBLE
        }else{
            diceViewList[diceIndex].visibility = VISIBLE
        }
    }

    /**
     * Updates the images of the dice views
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateDiceImages(){
        for(i in 0 until diceViewList.size){
            diceViewList[i].setImageResource(viewModel.game.dices[i].image)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(GAME_STATE, viewModel.game)
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_ROUND, viewModel.game.currentRound)
        outState.putInt(NBR_THROWS, viewModel.game.nbrOfThrows)
        outState.putBoolean(SPINNER_ENABLED, viewModel.spinnerIsEnabled)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_reset -> {
            //Resets the game by calling on MainActivity again
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}