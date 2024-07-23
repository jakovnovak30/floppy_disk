package hr.jakovnovak.games.floppydisk.ui.main

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import hr.jakovnovak.games.floppydisk.R
import hr.jakovnovak.games.floppydisk.ui.main.popups.GameOverFragment

class GameSurfaceView(context : Context, attrs : AttributeSet? = null):
    SurfaceView(context, attrs), SurfaceHolder.Callback, GameStateListener {
    private val background : Bitmap = ResourcesCompat.getDrawable(context.resources,
                                        if(context.getSharedPreferences("floppy_disk", MODE_PRIVATE).getBoolean("darkMode", false))
                                                R.drawable.background_night else R.drawable.background,
                                        context.theme)
                                    ?.toBitmap() ?: throw IllegalStateException()
    private val floppyDiskSprite : Bitmap = ResourcesCompat.getDrawable(context.resources, R.drawable.floppy_disk, context.theme)
                                                          ?.toBitmap() ?: throw IllegalStateException()
    private val computerSprite : Bitmap = ResourcesCompat.getDrawable(context.resources, R.drawable.computer_flipped, context.theme)
                                                        ?.toBitmap() ?: throw IllegalStateException()
    private val cdSprite : Bitmap = ResourcesCompat.getDrawable(context.resources, R.drawable.cd, context.theme)
                                                  ?.toBitmap() ?: throw IllegalStateException()
    private val rect = Rect()

    internal val game : Game
    private val gameThread : Thread
    private var score = 0
    private val textPaint = Paint()
    private val textPath = Path()

    init {
        Log.d(null, "GameSurfaceView Initialized!")
        holder.addCallback(this)
        game = Game(this)
        game.attach(this)
        gameThread = Thread {
            try {
                game.gameLoop()
            } catch (_: InterruptedException) { /* stop the thread */
            }

            // TODO: game over screen...
        }

        textPaint.color = Color.valueOf(99f / 255, 199f / 255, 77f / 255).toArgb()
        textPaint.textSize = 70f
        textPaint.typeface = resources.getFont(R.font.zx_spectrum)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.set(0, 0, width, height)
        canvas.drawBitmap(background, null, rect, null)

        val convertCoords : (Obstacle) -> List<Int> = {
            listOf(
                ((it.x + 1f)/2 * this.width).toInt(),
                ((it.y + 1f)/2 * this.height).toInt(),
                ((it.x + it.width + 1f)/2 * this.width).toInt(),
                ((it.y + it.height + 1f)/2 * this.height).toInt()
            )
        }

        val (floppyX, floppyY, floppyX2, floppyY2) = convertCoords(Obstacle(game.floppy.x, game.floppy.y,
                                                                            Game.floppyHeight, Game.floppyWidth))
        rect.set(floppyX, floppyY, floppyX2, floppyY2)
        canvas.drawBitmap(floppyDiskSprite, null, rect, null)

        game.towers.forEach {
            val (towerX, towerY, towerX2, towerY2) = convertCoords(it)
            val deltaX = towerX2 - towerX
            val deltaY = towerY2 - towerY

            rect.set(towerX, towerY, towerX2, towerY2)

            canvas.drawBitmap(computerSprite, null, rect, null)

            textPath.addRect(rect.toRectF(), Path.Direction.CW)
            canvas.drawTextOnPath(String.format("%03d", score), textPath,
                deltaX.toFloat()/5, deltaY.toFloat() * 3/5, textPaint)
            textPath.reset()
        }

        game.cds.forEach {
            val (cdX, cdY, cdX2, cdY2) = convertCoords(it)

            rect.set(cdX, cdY, cdX2, cdY2)
            canvas.drawBitmap(cdSprite, null, rect, null)
        }
    }

    fun update() {
        val canvas : Canvas = holder.lockCanvas()
        this.onDraw(canvas)
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setOnClickListener {
            game.setVelocity(0.1f)
        }

        gameThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d("surface", "Surface changed")
        // TODO: istraži kak ovo dela
        return
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        game.detach(this)
        gameThread.interrupt()
    }

    override fun scoreChanged(newScore: Int) {
        this.score = newScore
        // TODO: if score > 999, easter egg porukica
    }

    override fun gameOver(score: Int) {
        var color = Color.WHITE
        while(game.diskFalling()) {
            val canvas : Canvas = holder.lockCanvas()
            this.onDraw(canvas)
            canvas.drawColor(color, BlendMode.PLUS)
            color = (color + Color.BLACK) / 2
            holder.unlockCanvasAndPost(canvas)
            Thread.sleep(game.fpsInterval.toLong())
        }
    }
}