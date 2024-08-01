package hr.jakovnovak.games.floppydisk

import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import hr.jakovnovak.games.floppydisk.ui.main.GameStateListener
import hr.jakovnovak.games.floppydisk.ui.main.GameSurfaceView
import hr.jakovnovak.games.floppydisk.ui.main.popups.GameOverFragment
import hr.jakovnovak.games.floppydisk.ui.main.popups.GamePausedFragment

class GameActivity : FragmentActivity() {
    internal lateinit var gameSurfaceView: GameSurfaceView
    private val backgroundSongs = listOf(
        R.raw.background_funk,
        R.raw.background_adventure,
        R.raw.background_nostalgia,
        R.raw.background_hope,
        R.raw.background_surf
    )
    internal lateinit var mediaPlayer: MediaPlayer

    private lateinit var soundPool: SoundPool
    private val soundEffects : MutableMap<String, Int> = mutableMapOf()

    private lateinit var sharedPreferences: SharedPreferences
    private var highScoreChanged = false

    private val listener = object : GameStateListener {
        override fun scoreChanged(newScore : UInt) {
            val currHighScore = sharedPreferences.getInt("highScore", 0).toUInt()

            if(newScore > currHighScore) {
                highScoreChanged = true
                sharedPreferences.edit()
                    .putInt("highScore", newScore.toInt())
                    .apply()
            }
        }
        override fun gameOver(score : UInt) {
            val popupFragment = GameOverFragment(highScoreChanged)
            popupFragment.apply {
                isCancelable = false
                show(supportFragmentManager, "GAME_OVER")
            }
        }

        override fun cdCreated() { playEffect("CD_EFFECT", priority = 2) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameSurfaceView = findViewById(R.id.gameSurfaceView)
        gameSurfaceView.isVisible = true
        gameSurfaceView.game.attach(listener)

        this.onBackPressedDispatcher.addCallback {
            gameSurfaceView.gameThread.interrupt()
            val popupFragment = GamePausedFragment()
            popupFragment.apply {
                isCancelable = false
                show(supportFragmentManager, "GAME_PAUSED")
            }
        }

        // shared preferences
        sharedPreferences = getSharedPreferences("floppy_disk", MODE_PRIVATE)

        // sound
        mediaPlayer = MediaPlayer.create(this, backgroundSongs.random())
        mediaPlayer.start()

        soundPool = SoundPool.Builder()
            .setMaxStreams(16)
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .build())
            .build()

        soundPool.apply {
            soundEffects["CD_EFFECT"] = load(this@GameActivity, R.raw.cd_shot, 1)
            soundEffects["GAME_OVER_EFFECT"] = load(this@GameActivity, R.raw.fail_sound, 1)
            soundEffects["JUMP_EFFECT"] = load(this@GameActivity, R.raw.jump_sound, 1)
        }
    }

    internal fun playEffect(effect : String, volume : Float = 1f, priority : Int = 1) {
        if(!soundEffects.containsKey(effect))
            throw IllegalStateException("Sound effect does not exist!")

        soundPool.play(soundEffects[effect] ?: throw IllegalStateException(),
            volume, volume, priority, 0, 1f)
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameSurfaceView.game.detach(listener)
    }
}