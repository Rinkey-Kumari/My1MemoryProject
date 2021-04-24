package com.rk.mymemory.models

import com.rk.mymemory.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BordSize) {


    val cards: List<MemoryCard>
    var numPairsFound = 0
    var indexOfSingleSelectedCard : Int? = null
    private var numCardsFlip = 0

    init {
        val choosenImages : List <Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomImages : List<Int> = (choosenImages + choosenImages).shuffled()
        cards = randomImages.map { MemoryCard(it) }
    }

    fun filpCard(position: Int) : Boolean{
        numCardsFlip++
        val cards: MemoryCard = cards[position]
        var foundMatch : Boolean = false
         if(indexOfSingleSelectedCard == null) {
             //0 or 2 priviously card fliped : restore the card + flip the card
             restoreCards()
             indexOfSingleSelectedCard = position
         } else {
             // 1 previously card fliped
             foundMatch = isMatchedFound(indexOfSingleSelectedCard!!, position)
             indexOfSingleSelectedCard = null
         }
        cards.isFaced = !cards.isFaced
        return foundMatch
    }

    private fun isMatchedFound(position1: Int, position2: Int): Boolean {
        if (cards[position1].intendifier != cards[position2].intendifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(card:MemoryCard in cards) {
            if(!card.isMatched){
                card.isFaced = false
            }
        }
    }

    fun hasWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()

    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaced == true
    }

    fun getNumMoves(): Int {
        return numCardsFlip / 2

    }
}