package hr.jakovnovak.games.floppydisk.ui.main

interface GameStateListener {
    fun scoreChanged(newScore : UInt)
    fun gameOver(score : UInt)
    fun cdCreated()
}