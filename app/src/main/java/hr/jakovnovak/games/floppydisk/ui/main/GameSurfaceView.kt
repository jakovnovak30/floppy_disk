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
    private var ready : Boolean = false

    init {
        Log.d(null, "GameSurfaceView Initialized!")
        holder.addCallback(this)
    }

    // crta samo pozadinu
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.set(0, 0, width, height)
        canvas.drawBitmap(background, null, rect, null)
    }

    fun updateView(floppy : FloppyDisk, towers : List<Tower>) {
        if(!ready)
            return
        val canvas = holder.lockCanvas()

        rect.set(0, 0, width, height)
        canvas.drawBitmap(background, null, rect, null)

        val floppyX = ((floppy.x + 1f)/2 * this.width).toInt()
        val floppyY = ((floppy.y + 1f)/2 * this.height).toInt()
        val floppyWidth = (0.2f * this.width).toInt()
        val floppyHeight = (0.1f * this.height).toInt()
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

        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isVisible = true
        ready = true
        val canvas : Canvas = holder.lockCanvas()
        draw(canvas)
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        return
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        return
    }
}