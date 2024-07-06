package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import hr.jakovnovak.games.floppydisk.ui.main.MainViewModel

class MainActivity : Activity() {
    private var aboutVisible : Boolean = false
    private lateinit var rootView: View
    private lateinit var startButton : Button
    private lateinit var aboutButton : Button
    private lateinit var textArea : TextView

    private fun updateBackground() {
        rootView.background = ResourcesCompat.getDrawable(baseContext.resources,
                                    if(MainViewModel.darkMode) R.drawable.background_night else R.drawable.background,
                                    baseContext?.theme)

        val newColor : Int = if(MainViewModel.darkMode) Color.DKGRAY else Color.LTGRAY
        val newTextColor : Int = if(MainViewModel.darkMode) Color.WHITE else Color.BLACK
        startButton.setBackgroundColor(newColor)
        aboutButton.setBackgroundColor(newColor)

        startButton.setTextColor(newTextColor)
        aboutButton.setTextColor(newTextColor)
        textArea.setTextColor(newTextColor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById<Button>(R.id.playGameButton)
        aboutButton = findViewById<Button>(R.id.aboutGameButton)
        val darkModeButton = findViewById<Button>(R.id.darkModeButton)
        textArea = findViewById<TextView>(R.id.mainMenuTitle)
        rootView = findViewById<View>(R.id.container)
        updateBackground()

        startButton.setOnClickListener {
            textArea.text = "Starting game..."

            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        aboutButton.setOnClickListener {
            aboutVisible = !aboutVisible

            textArea.text = if(aboutVisible) "Simple Flappy bird clone made by: @jakovnovak30" else "Floppy Disk"
        }

        darkModeButton.setOnClickListener {
            MainViewModel.darkMode = !MainViewModel.darkMode
            updateBackground()
        }
    }

    override fun onResume() {
        super.onResume()
        textArea.text = "Floppy Disk"
        aboutVisible = false
    }
}