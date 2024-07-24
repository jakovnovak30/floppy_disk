package hr.jakovnovak.games.floppydisk

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import hr.jakovnovak.games.floppydisk.ui.main.GameStateListener
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView
import hr.jakovnovak.games.floppydisk.ui.main.popups.GameOverFragment

class GameActivity : FragmentActivity() {
    private lateinit var gameSurfaceView: GameSurfaceView
    private val listener = object : GameStateListener {
        override fun scoreChanged(newScore : Int) { } // TODO: provjeri ak je haj skor!
        override fun gameOver(score : Int) {
            val popupFragment = GameOverFragment()
            popupFragment.isCancelable = false
            popupFragment.show(supportFragmentManager, "GAME_OVER")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        //scoreText = findViewById(R.id.scoreText)
        val darkMode = baseContext.getSharedPreferences("floppy_disk", MODE_PRIVATE)
            .getBoolean("darkMode", false)
        //scoreText.setTextColor(if(darkMode) Color.WHITE else Color.BLACK)

        gameSurfaceView = findViewById(R.id.gameSurfaceView)
        gameSurfaceView.isVisible = true

        gameSurfaceView.game.attach(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameSurfaceView.game.detach(listener)
    }
}