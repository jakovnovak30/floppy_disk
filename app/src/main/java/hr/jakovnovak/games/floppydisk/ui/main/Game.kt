package hr.jakovnovak.games.floppydisk.ui.main

import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max

// koristimo normirane koordinate za x, y -> tj. idu od (-1, -1) do (1, 1)
data class FloppyDisk(var x : Float, var y : Float, var velocity : Float = -0.01f)
data class Tower(var x : Float, var y : Float, var height : Float, var width : Float)

class Game(private val view: GameSurfaceView) {
    val towers: MutableList<Tower> = mutableListOf()
    val floppy: FloppyDisk = FloppyDisk(-0.3f, 0f)

    private val listeners : MutableList<GameStateListener> = mutableListOf()

    // static variables
    companion object {
        const val towerSpacingHorizontal = 1f
        const val towerHeight = 0.2f
        const val towerWidth = 0.35f

        const val floppyHeight = 0.1f
        const val floppyWidth = 0.2f
    }

    fun attach(listener : GameStateListener) = listeners.add(listener)
    private fun notifyScore() = listeners.forEach { l -> l.scoreChanged(MainViewModel.score) }
    private fun notifyOver() = listeners.forEach { l -> l.gameOver(MainViewModel.score) }

    private fun testIntersect() : Boolean {
        if(floppy.y > 1f)
            return true

        val floppyRect = RectF(floppy.x, floppy.y, floppy.x + floppyWidth*2f, floppy.y + floppyHeight*2f)
        return towers.filter {
                val itRect = RectF(it.x, it.y, it.x + it.width*2f, it.y + it.height*2f)
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
            towers.add(Tower(i * towerSpacingHorizontal, -1f, towerHeight, towerWidth))
            towers.add(Tower(i * towerSpacingHorizontal, 1f - towerHeight*2f, towerHeight, towerWidth))
        }
        towers.map { t -> Tower(t.x + 0.5f, t.y, t.height, t.width) }

        val horizontalVelocity = 0.03f

        val desiredFps = 60f
        val fpsInterval : Float = 1000f / desiredFps
        var lastTime : Long = 0
        while(true) {
            floppy.y -= floppy.velocity * 0.5f
            val countBefore = towers.count { t -> t.x < -0.33f }
            towers.forEach { t -> t.x -= horizontalVelocity }
            val countAfter = towers.count { t -> t.x < -0.33f }

            if(countAfter != countBefore)
                MainViewModel.score++

            // gotova igra
            if(testIntersect())
                break

            floppy.velocity = max(floppy.velocity - 0.01f, -0.03f)
            towers.forEach { t ->
                if(t.x < -1f)
                    t.x = 1f
            }

            view.update()
            /*
            val currTime = System.currentTimeMillis()
            if(currTime - lastTime < fpsInterval) {
                Thread.sleep(abs(fpsInterval - (currTime-lastTime).toFloat()).toLong())
            }
            lastTime = System.currentTimeMillis()
             */
            Thread.sleep(fpsInterval.toLong())
        }

        // GAME OVER
        notifyOver()
    }
}