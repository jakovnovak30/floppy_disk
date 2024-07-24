package hr.jakovnovak.games.floppydisk

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import hr.jakovnovak.games.floppydisk.ui.main.GameStateListener
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView
import hr.jakovnovak.games.floppydisk.ui.main.popups.GameOverFragment
import hr.jakovnovak.games.floppydisk.ui.main.popups.GamePausedFragment

class GameActivity : FragmentActivity() {
    internal lateinit var gameSurfaceView: GameSurfaceView
    private val listener = object : GameStateListener {
        override fun scoreChanged(newScore : Int) { } // TODO: provjeri ak je haj skor!
        override fun gameOver(score : Int) {
            val popupFragment = GameOverFragment()
            popupFragment.apply {
                isCancelable = false
                show(supportFragmentManager, "GAME_OVER")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameSurfaceView = findViewById(R.id.gameSurfaceView)
        gameSurfaceView.isVisible = true
        gameSurfaceView.game.attach(listener)

        this.onBackPressedDispatcher.addCallback {
            gameSurfaceView.gameThread.interrupt()
            val popupFragment = GamePausedFragment()
            popupFragment.apply{
                isCancelable = false
                show(supportFragmentManager, "GAME_PAUSED")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameSurfaceView.game.detach(listener)
    }
}