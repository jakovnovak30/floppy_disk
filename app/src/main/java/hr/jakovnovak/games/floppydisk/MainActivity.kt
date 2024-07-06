package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    private var ctr : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.playGameButton);
        val textArea = findViewById<TextView>(R.id.mainMenuTitle);

        startButton.setOnClickListener {
            textArea.text = "Game should start now!"
            ctr = (ctr ?: 0) + 1

            if ((ctr ?: 0) > 10)
                textArea.text = "No game for you!"
        }
    }
}