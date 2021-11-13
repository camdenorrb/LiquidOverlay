package dev.twelveoclock.soundoverlayplugin

import dev.twelveoclock.liquidoverlay.api.OverlayPlugin
import dev.twelveoclock.soundoverlayplugin.modules.SoundModule
import java.awt.Color
import java.awt.image.BufferedImage
import javax.sound.sampled.AudioFormat

object SoundOverlayPlugin : OverlayPlugin() {

    val soundModule = SoundModule()


    override fun onEnable() {
        soundModule.enable()
    }

    override fun onDisable() {
        soundModule.disable()
    }


    override fun draw() {

        // TODO: MOO COWWWWWWWWW, place your audio here
        val audioInputData = ByteArray(1_000)
        val (peakLeft, peakRight) = soundModule.tick(AudioFormat(44100f, 16, 1, true, false), audioInputData)

        val bufferedImage = BufferedImage(overlay.canvasWidth, overlay.canvasHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        if (peakLeft > soundModule.averagePeakLeft()) {
            graphics.color = Color(100, 50, 0)
            graphics.fillRect(0, bufferedImage.height / 2, 50, 100)
        }
        if (peakRight > soundModule.averagePeakLeft()) {
            graphics.color = Color(100, 50, 0)
            graphics.fillRect(bufferedImage.width - 50, bufferedImage.height / 2, 50, 100)
        }

        graphics.dispose()
    }

}