package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import hr.jakovnovak.games.floppydisk.ui.main.Game
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView

class GameActivity : Activity() {
    private lateinit var gameSurfaceView: GameSurfaceView
    private lateinit var thread : Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        gameSurfaceView = findViewById<GameSurfaceView>(R.id.gameSurfaceView)

        val game : Game = Game(gameSurfaceView)

        val runnable = Runnable {
            game.gameLoop()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        gameSurfaceView.setOnClickListener {
            game.setVelocity(0.1f)
        }

        thread = Thread(runnable)
        thread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.join(10)
    }
}