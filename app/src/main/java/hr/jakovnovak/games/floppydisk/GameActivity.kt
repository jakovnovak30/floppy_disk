package hr.jakovnovak.games.floppydisk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.jakovnovak.games.floppydisk.ui.main.GameFragment

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GameFragment.newInstance())
                .commitNow()
        }
    }
}