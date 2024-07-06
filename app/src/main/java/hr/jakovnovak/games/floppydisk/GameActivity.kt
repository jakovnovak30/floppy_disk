package hr.jakovnovak.games.floppydisk

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)

        surfaceView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val bitmap : Bitmap? = ResourcesCompat.getDrawable(baseContext.resources, R.drawable.background, baseContext?.theme)
                                              ?.toBitmap(config = null)
        if(bitmap == null)
            throw IllegalStateException()

        val newFrame = Canvas(bitmap)
        surfaceView.draw(newFrame)
    }
}