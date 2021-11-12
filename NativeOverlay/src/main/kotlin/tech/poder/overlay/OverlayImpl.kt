package tech.poder.overlay

import java.awt.image.BufferedImage

class OverlayImpl(val selected: WindowManager) : Overlay {

    private val currentRectStorage = run {
        val rectStorage = RectReader.createSegment()

        selected.getWindowRect(rectStorage)

        rectStorage
    }

    private var rectReader = RectReader.fromMemorySegment(currentRectStorage)

    override var canvasWidth: Int = rectReader.width.toInt()

    override var canvasHeight: Int = rectReader.height.toInt()

    private var internal = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB)
    private var internalGraphics = internal.graphics


    private fun remake() {
        selected.getWindowRect(currentRectStorage)
        rectReader = RectReader.fromMemorySegment(currentRectStorage)
        canvasWidth = rectReader.width.toInt()
        canvasHeight = rectReader.height.toInt()
        val old = internalGraphics
        val scaled = internal.getScaledInstance(canvasWidth, canvasHeight, BufferedImage.SCALE_SMOOTH)
        internal = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB)
        internalGraphics = internal.graphics
        internalGraphics.drawImage(scaled, 0, 0, null)
        old.dispose()
    }

    override fun image(image: BufferedImage, position: Overlay.Position, width: Int, height: Int) {
        internalGraphics.drawImage(image, position.x, position.y, width, height, null)
    }

    override fun clear() {
        internalGraphics.color = WindowManager.invisibleColor
        internalGraphics.fillRect(0, 0, canvasWidth, canvasHeight)
    }

    override fun close() {
        internalGraphics.dispose()
        currentRectStorage.segment.scope().close()
    }

    override fun show() {
        selected.showWindow()
    }

    override fun hide() {
        selected.hideWindow()
    }

    override fun onResize(callback: (Overlay) -> Unit) {
        remake()
        callback.invoke(this)
    }
}