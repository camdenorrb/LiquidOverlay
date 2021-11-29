package dev.twelveoclock.soundoverlayplugin

import dev.twelveoclock.liquidoverlay.api.OverlayPlugin
import dev.twelveoclock.soundoverlayplugin.modules.SoundModule
import tech.poder.overlay.video.Overlay
import java.awt.Color
import java.awt.image.BufferedImage

object SoundOverlayPlugin : OverlayPlugin() {

    val soundModule = SoundModule()


    override fun onEnable() {
        soundModule.enable()
        println("SoundOverlayPlugin enabled")
    }

    override fun onDisable() {
        soundModule.disable()
    }


    override fun draw() {

        // TODO: MOO COWWWWWWWWW, place your audio here
        /*
        val audioInputData = ByteArray(1_000)
        val (peakLeft, peakRight) = soundModule.tick(AudioFormat(44100f, 16, 1, true, false), audioInputData)

        val bufferedImage = BufferedImage(overlay.canvasWidth, overlay.canvasHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        if (peakLeft > soundModule.averagePeakLeft()) {
            graphics.color = Color(100, 50, 0)
            graphics.fillRect(0, bufferedImage.height / 2, 50, 100)
        }
        if (peakRight > soundModule.averagePeakRight()) {
            graphics.color = Color(100, 50, 0)
            graphics.fillRect(bufferedImage.width - 50, bufferedImage.height / 2, 50, 100)
        }
*/
        // Testing


        val bufferedImage = BufferedImage(overlay.canvasWidth, overlay.canvasHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        // Draw transparent background
        graphics.color = Color(255, 0, 0)
        graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

        // Draw left/right bars
        graphics.color = Color(100, 50, 0)
        graphics.fillRect(0, 0, 5, bufferedImage.height)
        graphics.fillRect(bufferedImage.width - 5, 0, 5, bufferedImage.height)

        overlay.image(bufferedImage, Overlay.Position(0, 0), overlay.canvasWidth, overlay.canvasHeight)

        graphics.dispose()

        /*
        val bufferedImage = BufferedImage(overlay.canvasWidth, overlay.canvasHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        graphics.color = java.awt.Color(0, 100, 0)
        graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

        overlay.image(bufferedImage, Overlay.Position(0, 0), overlay.canvasWidth, overlay.canvasHeight)

        graphics.dispose()
        */
    }

}