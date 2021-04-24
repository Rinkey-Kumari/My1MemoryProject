package com.rk.mymemory

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Adapter
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rk.mymemory.models.BordSize
import com.rk.mymemory.models.MemoryCard
import com.rk.mymemory.models.MemoryGame
import com.rk.mymemory.utils.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG = "MainActivity"
    }

    private lateinit var memoryGame : MemoryGame
    private lateinit var adapter: MemoryBoardGame
    private lateinit var rvBoard : RecyclerView
    private lateinit var tvNumMoves : TextView
    private lateinit var tvNumPairs : TextView
    private lateinit var clView : ConstraintLayout

    private var bordSize: BordSize = BordSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        clView = findViewById(R.id.clView)

        boardSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mi_refresh -> {//Reload the board
                if(memoryGame.getNumMoves() > 0 && !memoryGame.hasWonGame()) {
                    showAlertDialog("Quite the game ?", null, View.OnClickListener {
                        boardSetup()
                    })
                } else {
                    boardSetup()
                }
                return true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val bordSizeDialog = LayoutInflater.from(this).inflate(R.layout.dialog_bord_size, null)
        val radioGroupSize:RadioGroup = bordSizeDialog.findViewById<RadioGroup>(R.id.radioGroup)
        when(bordSize) {
            BordSize.EASY -> radioGroupSize.check(R.id.easy)
            BordSize.MEDIUM -> radioGroupSize.check(R.id.medium)
            BordSize.HARD -> radioGroupSize.check(R.id.hard)
        }
        showAlertDialog("Choose new Size" , bordSizeDialog, View.OnClickListener {
            //set the new size
            bordSize = when(radioGroupSize.checkedRadioButtonId) {
                R.id.easy -> BordSize.EASY
                R.id.medium -> BordSize.MEDIUM
                else -> BordSize.HARD
            }
            boardSetup()
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickLintener : View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                positiveClickLintener.onClick(null)
            }.show()

    }
    private fun boardSetup() {
        when(bordSize) {
            BordSize.EASY -> {
                tvNumMoves.text = "EASY 4 X 2"
                tvNumPairs.text = "Pairs : 0 / 4"
            }
            BordSize.MEDIUM -> {
                tvNumMoves.text = "MEDIUM 6 X 3"
                tvNumPairs.text = "Pairs : 0 / 9"
            }
            BordSize.HARD -> {
                tvNumMoves.text = "HARD 6 X 4"
                tvNumPairs.text = "Pairs : 0 / 12"
            }
        }

        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_null))
        memoryGame = MemoryGame(bordSize)
        adapter = MemoryBoardGame(this, bordSize, memoryGame.cards, object: MemoryBoardGame.CardClickListener{
            override fun onCardClickListnerer(position: Int) {
                Log.d(TAG, "Click card $position")
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, bordSize.getWidth())
    }
    private fun updateGameWithFlip(position: Int) {
        //Error checking
        //1. If the user won Game
        //2. if the card is faced up
        if(memoryGame.hasWonGame()) {
            // Invalied move
            Snackbar.make(clView, "You already won the game" , Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)) {
            //Already faced up
            Snackbar.make(clView, "Wrong move" , Snackbar.LENGTH_LONG).show()
            return
        }

        if (memoryGame.filpCard(position)) {
            Log.d(TAG,"Found a match Num of pair found ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / bordSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_null),
                ContextCompat.getColor(this, R.color.color_progress_full)
            )as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs : ${memoryGame.numPairsFound} / ${bordSize.getNumPairs()}"
            if (memoryGame.hasWonGame()) {
                showAlertDialog("Congratulation you have won game. Do you want to play again?", null,View.OnClickListener {
                    boardSetup()
                })

                //Snackbar.make(clView,"Congrates you have win Game", Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text = "Moves : ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}