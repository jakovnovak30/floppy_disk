package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isVisible
import hr.jakovnovak.games.floppydisk.ui.main.Game
import hr.jakovnovak.games.floppydisk.ui.main.GameStateListener
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView

class GameActivity : Activity() {
    private lateinit var gameSurfaceView: GameSurfaceView
    private lateinit var scoreText : TextView
    private val listener = object : GameStateListener {
        override fun scoreChanged(newScore : Int) { } // TODO: provjeri ak je haj skor!
        override fun gameOver(score : Int) {
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
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