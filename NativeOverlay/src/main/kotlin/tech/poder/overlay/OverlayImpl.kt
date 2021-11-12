package tech.poder.overlay

import java.awt.Color
import java.awt.image.BufferedImage

class OverlayImpl(val selected: Process) : Overlay {

    override var canvasWidth: Int = 0

    override var canvasHeight: Int = 0


    override fun rectangle(color: Color, first: Overlay.Position, second: Overlay.Position) {
        TODO("Not yet implemented")
    }

    override fun circle(color: Color, radius: Int, center: Overlay.Position) {
        TODO("Not yet implemented")
    }

    override fun image(image: BufferedImage, position: Overlay.Position, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun text(value: String, fontSize: Float, color: Color, start: Overlay.Position) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun show() {
        TODO("Not yet implemented")
    }

    override fun onResize(callback: () -> Unit) {
        TODO("Not yet implemented")
    }
}