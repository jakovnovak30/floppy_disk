package hr.jakovnovak.games.floppydisk.ui.main

import androidx.lifecycle.ViewModel

object MainViewModel : ViewModel() {
    var darkMode : Boolean = false
    var score : Int = 0
        set(value) {
            if(value < field)
                throw IllegalArgumentException()
            field = value
        }
}