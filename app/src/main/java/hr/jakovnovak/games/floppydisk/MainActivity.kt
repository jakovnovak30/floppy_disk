package hr.jakovnovak.games.floppydisk

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import hr.jakovnovak.games.floppydisk.databinding.ActivityMainBinding
import hr.jakovnovak.games.floppydisk.ui.main.popups.SettingsFragment

class MainActivity : FragmentActivity() {
    private var aboutVisible : Boolean = false
    private var darkMode : Boolean = false
    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private var highScore : UInt = 0u

    private fun updateBackground() {
        binding.root.background = ResourcesCompat.getDrawable(baseContext.resources,
                                    if(darkMode) R.drawable.background_night else R.drawable.background,
                                    baseContext?.theme)

        val newColor : Int = if(darkMode) Color.DKGRAY else Color.LTGRAY
        val newTextColor : Int = if(darkMode) Color.WHITE else Color.BLACK
        binding.playGameButton.setBackgroundColor(newColor)
        binding.aboutGameButton.setBackgroundColor(newColor)

        binding.playGameButton.setTextColor(newTextColor)
        binding.aboutGameButton.setTextColor(newTextColor)
        binding.mainMenuTitle.setTextColor(newTextColor)
        binding.highScore.setTextColor(newTextColor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // back
        this.onBackPressedDispatcher.addCallback { this@MainActivity.finishAffinity() }

        // init shared preferences
        sharedPreferences = baseContext.getSharedPreferences("floppy_disk", MODE_PRIVATE)

        // check high score
        highScore = sharedPreferences.getInt("highScore", 0).toUInt()
        if(highScore != 0u) {
            binding.highScore.text = "High score: $highScore"
            binding.highScore.visibility = View.VISIBLE
        }

        // check dark/light mode
        darkMode = sharedPreferences.getBoolean("darkMode", false)
        updateBackground()

        binding.playGameButton.setOnClickListener {
            binding.mainMenuTitle.text = "Starting game..."
            binding.highScore.visibility = View.GONE

            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.aboutGameButton.setOnClickListener {
            aboutVisible = !aboutVisible

            binding.mainMenuTitle.text = if(aboutVisible) "Simple Flappy bird clone made by: @jakovnovak30" else "Floppy Disk"
            binding.highScore.visibility = if(aboutVisible || highScore == 0u) View.GONE else View.VISIBLE
        }

        binding.darkModeButton.setOnClickListener {
            darkMode = !darkMode
            val editor = sharedPreferences.edit()
            editor.putBoolean("darkMode", darkMode)
            editor.apply()
            updateBackground()
        }

        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val popupFragment = SettingsFragment()
            popupFragment.show(supportFragmentManager, "SETTINGS")
        }

        // sound
        mediaPlayer = MediaPlayer.create(this, R.raw.eight_bit_menu)
        mediaPlayer.apply {
            isLooping = true
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mainMenuTitle.text = "Floppy Disk"
        if(highScore != 0u)
            binding.highScore.visibility = View.VISIBLE
        aboutVisible = false
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }
}