package hr.jakovnovak.games.floppydisk

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

class MainActivity : Activity() {
    private var aboutVisible : Boolean = false
    private var darkMode : Boolean = false
    private lateinit var rootView: View
    private lateinit var startButton : Button
    private lateinit var aboutButton : Button
    private lateinit var textArea : TextView
    private lateinit var logo : GLSurfaceView
    private lateinit var sharedPreferences: SharedPreferences

    private fun updateBackground() {
        rootView.background = ResourcesCompat.getDrawable(baseContext.resources,
                                    if(darkMode) R.drawable.background_night else R.drawable.background,
                                    baseContext?.theme)

        val newColor : Int = if(darkMode) Color.DKGRAY else Color.LTGRAY
        val newTextColor : Int = if(darkMode) Color.WHITE else Color.BLACK
        startButton.setBackgroundColor(newColor)
        aboutButton.setBackgroundColor(newColor)

        startButton.setTextColor(newTextColor)
        aboutButton.setTextColor(newTextColor)
        textArea.setTextColor(newTextColor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.playGameButton)
        aboutButton = findViewById(R.id.aboutGameButton)
        val darkModeButton = findViewById<Button>(R.id.darkModeButton)
        textArea = findViewById(R.id.mainMenuTitle)
        rootView = findViewById(R.id.container)
        logo = findViewById(R.id.logo)

        sharedPreferences = baseContext.getSharedPreferences("floppy_disk", MODE_PRIVATE)
        darkMode = sharedPreferences.getBoolean("darkMode", false)
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
            darkMode = !darkMode
            val editor = sharedPreferences.edit()
            editor.putBoolean("darkMode", darkMode)
            editor.apply()
            updateBackground()
        }
    }

    override fun onResume() {
        super.onResume()
        textArea.text = "Floppy Disk"
        aboutVisible = false
    }

    override fun onPause() {
        super.onPause()
    }
}