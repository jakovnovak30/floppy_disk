package hr.jakovnovak.games.floppydisk.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private val rect = Rect()

    init {
        Log.d(null, "GameSurfaceView Initialized!")
        holder.addCallback(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(null, "I'm trying to draw something!!")

        rect.set(0, 0, width, height)
        canvas.drawBitmap(background, null, rect, null)

        val x = 80
        val y = 80
        val radius = 40
        val paint = Paint()
        // Use Color.parseColor to define HTML colors
        paint.color = Color.parseColor("#CD5C5C")
        canvas.drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), paint)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(null, "Unutar ove funkcije sam!")
        isVisible = true
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