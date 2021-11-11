package tech.poder.overlay

import java.awt.image.BufferedImage

class OverlayImpl(val selected: Process) : Overlay {
    override var canvasWidth: Int = 0
    override var canvasHeight: Int = 0

    override fun image(image: BufferedImage, position: Overlay.Position, width: Int, height: Int) {
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