package dev.twelveoclock.soundoverlayplugin

import dev.twelveoclock.liquidoverlay.api.OverlayPlugin
import tech.poder.overlay.Overlay
import java.awt.Color
import java.awt.image.BufferedImage

object SoundOverlayPlugin : OverlayPlugin() {

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    override fun draw() {

        val bufferedImage = BufferedImage(overlay.canvasWidth, overlay.canvasHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        graphics.color = Color(0, 100, 0)
        graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

        overlay.image(bufferedImage, Overlay.Position(0, 0), overlay.canvasWidth, overlay.canvasHeight)

        graphics.dispose()
    }

}