package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.os.Bundle
import hr.jakovnovak.games.floppydisk.ui.main.Game
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView

class GameActivity : Activity() {
    private lateinit var gameSurfaceView: GameSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        gameSurfaceView = findViewById<GameSurfaceView>(R.id.gameSurfaceView)

        val game : Game = Game(gameSurfaceView)

        val thread = Runnable {
            game.gameLoop()
            // TODO: eventualno neki game over screen?
        }

        gameSurfaceView.setOnClickListener {
            game.setVelocity(0.1f)
        }

        //thread.run()
    }
}