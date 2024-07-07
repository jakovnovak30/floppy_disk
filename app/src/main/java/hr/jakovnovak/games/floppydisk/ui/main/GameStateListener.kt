package hr.jakovnovak.games.floppydisk.ui.main

interface GameStateListener {
    fun scoreChanged(newScore : Int)
    fun gameOver(score : Int)
}