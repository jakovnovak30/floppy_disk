package hr.jakovnovak.games.floppydisk.ui.main

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max

// koristimo normirane koordinate za x, y -> tj. idu od (0, 0) do (1, 1)
data class FloppyDisk(var x : Float, var y : Float, var velocity : Float = -0.05f)
data class Tower(var x : Float, var y : Float, var height : Float, var width : Float)

class Game(private val view: GameSurfaceView) {
    val towers: MutableList<Tower> = mutableListOf<Tower>()
        get() = field
    val cds : MutableList<Tower> = mutableListOf<Tower>()
        get() = field
    val floppy: FloppyDisk = FloppyDisk(0.3f, 0f)
        get() = field

    private fun testIntersect() : Boolean {
        return false
        if(floppy.y > 1f)
            return true

        val floppyRect = RectF(floppy.x, floppy.y, floppy.x + 0.2f, floppy.y + 0.1f)
        /*
        return towers.filter {
            val itRect = RectF(it.x, it.y, it.x + it.width, it.y + it.height)

            return RectF.intersects(floppyRect, itRect) || itRect.contains(floppyRect)
            }.isNotEmpty()
         */
    }

    fun setVelocity(value : Float) {
        if(abs(value) > 0.2f)
            return
        floppy.velocity = value
    }

    fun gameLoop() {
        Thread.sleep(1000)
        for (i in 0..1) {
            towers.add(Tower(i * 0.5f, 0f, 0.2f, 0.35f))
            towers.add(Tower(i * 0.5f, 0.8f, 0.2f, 0.35f))
        }
        towers.map { t -> Tower(t.x + 0.5f, t.y, t.height, t.width) }

        val horizontalVelocity = 0.03f
        while(true) {
            floppy.y -= floppy.velocity
            val countBefore = towers.count { t -> t.x < 0.33f }
            towers.forEach { t -> t.x -= horizontalVelocity }
            val countAfter = towers.count { t -> t.x < 0.33f }

            if(countAfter != countBefore)
                MainViewModel.score++

            // gotova igra
            if(testIntersect())
                break;

            floppy.velocity = max(floppy.velocity - 0.01f, -0.05f)
            towers.forEach { t ->
                if(t.x < 0f)
                    t.x = 1f
            }

            if(view.ready) {
                view.update()
            }
            Thread.sleep(30)
        }
    }
}