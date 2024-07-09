package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import hr.jakovnovak.games.floppydisk.ui.main.GameStateListener
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView

class GameActivity : Activity() {
    private lateinit var gameSurfaceView: GameSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        gameSurfaceView = findViewById(R.id.gameSurfaceView)
        gameSurfaceView.isVisible = true

        gameSurfaceView.game.attach(object : GameStateListener {
            override fun scoreChanged(newScore : Int) {
                // TODO: update text...
                return
            }
            override fun gameOver(score : Int) {
                val intent = Intent(this@GameActivity, MainActivity::class.java)
                startActivity(intent)
            }
        })
    }
}