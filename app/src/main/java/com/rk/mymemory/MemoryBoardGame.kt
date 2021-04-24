package com.rk.mymemory

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.rk.mymemory.models.BordSize
import com.rk.mymemory.models.MemoryCard
import com.rk.mymemory.models.MemoryGame
import kotlin.math.min

class MemoryBoardGame(
    private val context: Context,
    private val boardSize: BordSize,
    private val card: List<MemoryCard>,
    private val cardClickListener : CardClickListener
) :
    RecyclerView .Adapter<MemoryBoardGame.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardGame"
    }

    interface CardClickListener {
        fun onCardClickListnerer(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width/boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height/boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSize = min(cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParam = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParam.width = cardSize
        layoutParam.height = cardSize
        layoutParam.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards

    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        private var imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)


        fun bind (position: Int) {
            val memoryCard : MemoryCard = card[position]
            imageButton.setImageResource(if (memoryCard.isFaced) memoryCard.intendifier else R.drawable.ic_launcher_background)
            imageButton.alpha = if(memoryCard.isMatched) .4f else 1.0f
            var colorListStat : ColorStateList? = if(memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton, colorListStat)

            imageButton.setOnClickListener {
                Log.d(TAG, " Click on the Position $position")
                cardClickListener.onCardClickListnerer(position)
            }

        }
    }
}
