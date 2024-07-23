package hr.jakovnovak.games.floppydisk.ui.main

import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

// koristimo normirane koordinate za x, y -> tj. idu od (-1, -1) do (1, 1)
data class FloppyDisk(var x : Float, var y : Float, var velocity : Float = -0.01f)
data class Obstacle(var x : Float, var y : Float, var height : Float, var width : Float, var horizontalVelocity : Float = Game.horizontalVelocity)

class Game(private val view: GameSurfaceView) {
    val towers: MutableList<Obstacle> = mutableListOf()
    val cds : MutableList<Obstacle> = mutableListOf()
    val floppy: FloppyDisk = FloppyDisk(-0.6f, 0f)
    private var score = 0

    private val listeners : MutableList<GameStateListener> = mutableListOf()

    // static variables
    companion object {
        const val towerSpacingHorizontal = 1.5f
        const val towerHeight = 0.4f
        const val towerWidth = 0.65f

        const val floppyHeight = 0.12f
        const val floppyWidth = 0.24f

        const val cdHeight = 0.1f
        const val cdWidth  = 0.2f
        const val cdSpawnChance = 0.05f
        const val coolDowntime = 500 // milisekunde

        // defaultna vrijednost -> moremo ju menjati u @see Obstacle
        val horizontalVelocity = 0.03f
    }

    fun attach(listener : GameStateListener) = listeners.add(listener)
    fun detach(listener: GameStateListener) = listeners.remove(listener)
    private fun notifyScore() = listeners.forEach { l -> l.scoreChanged(score) }
    private fun notifyOver() = listeners.forEach { l -> l.gameOver(score) }

    private fun testIntersect() : Boolean {
        if(floppy.y > 1f)
            return true

        val intersectPredicate : (Obstacle) -> Boolean = {
            floppy.x + floppyWidth > it.x && floppy.x < it.x + it.width
         && floppy.y + floppyHeight > it.y && floppy.y < it.y + it.height
        }
        return towers.any(intersectPredicate) || cds.any(intersectPredicate)
    }

    fun setVelocity(value : Float) {
        if(abs(value) > 0.5f)
            throw IllegalArgumentException("Are you insane?")
        floppy.velocity = value
    }

    fun gameLoop() {
        for (i in 0..1) {
            towers.add(Obstacle(i * towerSpacingHorizontal, -1f, towerHeight, towerWidth))
            towers.add(Obstacle(i * towerSpacingHorizontal, 1f - towerHeight, towerHeight, towerWidth))
        }
        towers.map { t -> Obstacle(t.x + 0.5f, t.y, t.height, t.width) }

        val desiredFps = 80f
        val fpsInterval : Float = 1000f / desiredFps
        var lastTime : Long = 0

        while(true) {
            floppy.y -= floppy.velocity * 0.4f
            val countBefore = towers.count { t -> t.x < -0.33f }
            towers.forEach { it.x -= horizontalVelocity }
            cds.forEach {
                it.x -= it.horizontalVelocity
                it.horizontalVelocity += Random.nextFloat() / 100
            }
            val countAfter = towers.count { t -> t.x < -0.33f }

            if(countAfter != countBefore) {
                score++
                notifyScore()
            }

            // gotova igra
            if(testIntersect())
                break

            floppy.velocity = max(floppy.velocity - 0.01f, -0.03f)
            towers.forEach {
                if(it.x + it.width < -1f)
                    it.x = 1f
            }
            cds.removeAll { it.x + it.width < -1f }

            // postoji šansa da se spawna cd ako nema dovoljno cd-a
            // TODO: difficulty? potencijalno...
            val rand = Random.nextFloat()
            if(cds.size < 3 && rand < cdSpawnChance && System.currentTimeMillis() - lastTime > coolDowntime) {
                cds.add(Obstacle(1f, Random.nextFloat() - 0.5f, cdHeight, cdWidth, 0f))
                lastTime = System.currentTimeMillis()
            }

            view.update()
            Thread.sleep(fpsInterval.toLong())
        }

        // GAME OVER
        notifyOver()
    }
}