package com.ossiam.android.thirty.Model

import com.ossiam.android.thirty.R
import java.io.Serializable
import kotlin.collections.ArrayList

private const val MAX = 6
private const val MIN = 1
private const val STATE_NORMAL = 0
private const val STATE_SELECTED = 1
private const val STATE_MARKED = 2

class Dice : Serializable{
    var value: Int
    var image: Int = 0
    //private var selected: Boolean
    var state: Int = STATE_NORMAL
    set(state){
        when(state){
            0 -> {field = STATE_NORMAL
            }
            1 -> {field = STATE_SELECTED
            }
            2 -> {field = STATE_MARKED
            }
            else -> {
                //Nothing
            }
        }
    }
    private val diceList: ArrayList<Int> = ArrayList()

    init{
        for(i in MIN..MAX){
            diceList.add(R.drawable.white1)
            diceList.add(R.drawable.white2)
            diceList.add(R.drawable.white3)
            diceList.add(R.drawable.white4)
            diceList.add(R.drawable.white5)
            diceList.add(R.drawable.white6)
        }
        value = roll()
    }
    fun roll(): Int{
        if(state == STATE_NORMAL){
            value = (MIN..MAX).random()
            image = diceList[value-1]
        }
        return value
    }

}