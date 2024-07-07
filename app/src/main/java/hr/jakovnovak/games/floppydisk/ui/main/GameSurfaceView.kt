package hr.jakovnovak.games.floppydisk.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import hr.jakovnovak.games.floppydisk.MainActivity
import hr.jakovnovak.games.floppydisk.R


class GameSurfaceView(context : Context, attrs : AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    private val background : Bitmap = ResourcesCompat.getDrawable(context.resources,
                                        if(MainViewModel.darkMode) R.drawable.background_night else R.drawable.background,
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
    internal val gameThread : Thread

    init {
        Log.d(null, "GameSurfaceView Initialized!")
        holder.addCallback(this)
        game = Game(this)
        gameThread = Thread(Runnable {
            game.gameLoop()
            // TODO: game over screen...
        })
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("OVO je tag", "Unutar onDraw metode sam!")

        super.onDraw(canvas)
        rect.set(0, 0, width, height)
        canvas.drawBitmap(background, null, rect, null)

        val floppy = game.floppy
        val towers = game.towers

        val floppyX = ((floppy.x + 1f)/2 * this.width).toInt()
        val floppyY = ((floppy.y + 1f)/2 * this.height).toInt()
        val floppyWidth = (Game.floppyWidth * this.width).toInt()
        val floppyHeight = (Game.floppyHeight * this.height).toInt()
        rect.set(floppyX, floppyY, floppyX + floppyWidth, floppyY + floppyHeight)
        canvas.drawBitmap(floppyDiskSprite, null, rect, null)

        towers.forEach {
            val towerX = ((it.x + 1f)/2 * this.width).toInt()
            val towerY = ((it.y + 1f)/2 * this.height).toInt()
            val towerWidth = (it.width * this.width).toInt()
            val towerHeight = (it.height * this.height).toInt()

            rect.set(towerX, towerY, towerX + towerWidth, towerY + towerHeight)
            canvas.drawBitmap(computerSprite, null, rect, null)
        }
    }

    fun update() {
        val canvas = holder.lockCanvas()
        this.invalidate()
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setOnClickListener {
            game.setVelocity(0.1f)
        }

        gameThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // TODO: istraži kak ovo dela
        return
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameThread.interrupt()
        gameThread.join(1)
    }
}