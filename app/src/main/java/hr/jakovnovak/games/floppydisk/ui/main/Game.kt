package hr.jakovnovak.games.floppydisk.ui.main

import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max

// koristimo normirane koordinate za x, y -> tj. idu od (-1, -1) do (1, 1)
data class FloppyDisk(var x : Float, var y : Float, var velocity : Float = -0.01f)
data class Tower(var x : Float, var y : Float, var height : Float, var width : Float)

class Game(private val view: GameSurfaceView) {
    private val towers: MutableList<Tower> = mutableListOf<Tower>()
    private val floppy: FloppyDisk = FloppyDisk(-0.3f, 0f)

    private fun testIntersect() : Boolean {
        if(floppy.y > 1f)
            return true

        val floppyRect = RectF(floppy.x, floppy.y, floppy.x + 0.1f, floppy.y + 0.05f)
        return towers.filter {
                val itRect = RectF(it.x, it.y, it.x + it.width, it.y + it.height)
                return itRect.intersect(floppyRect) || itRect.contains(floppyRect)
            }.isNotEmpty()
    }

    fun setVelocity(value : Float) {
        if(abs(value) > 0.5f)
            throw IllegalArgumentException("Are you insane?")
        floppy.velocity = value
    }

    fun gameLoop() {
        for (i in 0..1) {
            towers.add(Tower(i * 1f, -1f, 0.2f, 0.35f))
            towers.add(Tower(i * 1f, 0.6f, 0.2f, 0.35f))
        }
        towers.map { t -> Tower(t.x + 0.5f, t.y, t.height, t.width) }

        val horizontalVelocity = 0.05f
        while(true) {
            floppy.y -= floppy.velocity
            val countBefore = towers.count { t -> t.x < -0.33f }
            towers.forEach { t -> t.x -= horizontalVelocity }
            val countAfter = towers.count { t -> t.x < -0.33f }

            if(countAfter != countBefore)
                MainViewModel.score++

            // gotova igra
            if(testIntersect())
                break;

            floppy.velocity = max(floppy.velocity - 0.01f, -0.03f)
            towers.forEach { t ->
                if(t.x < -1f)
                    t.x = 1f
            }

            view.updateView(floppy, towers)
            Thread.sleep(30)
        }
    }
}