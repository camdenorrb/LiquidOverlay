package tech.poder.overlay

import jdk.incubator.foreign.MemoryAddress
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.concurrent.write
import kotlin.io.path.Path

class OverlayImpl(private val self: WindowManager, private val selected: WindowManager) : Overlay {

    var onRedraw: (Overlay) -> Unit = {}

    private val currentRectStorage = run {
        val rectStorage = RectReader.createSegment()

        self.getWindowRect(rectStorage)

        rectStorage
    }

    val storageBitmap = Path("test.bmp").toAbsolutePath()
    val pathString = ExternalStorage.fromString(storageBitmap.toString())

    private var rectReader = RectReader.fromMemorySegment(currentRectStorage)

    override var canvasWidth = rectReader.width.toInt()

    override var canvasHeight = rectReader.height.toInt()

    private var prevList = NativeAPI.imageListCreate(canvasWidth, canvasHeight, 0x00000001, 1, 1) as MemoryAddress

    private var internal = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB)
    private var internalGraphics = internal.graphics

    private var visible = self.isVisible()

    private val checker = Thread {

        val newRectStorage = run {
            RectReader.createSegment().apply {
                self.getWindowRect(this)
            }
        }

        var prev = rectReader

        while (selected.isAlive()) {

            // FPS
            Thread.sleep(10)

            if (!selected.isVisible()) {
                self.hideWindow()
                continue
            }

            if (!self.isVisible()) {
                self.showWindow()
            }

            selected.getWindowRect(newRectStorage)

            val rect = RectReader.fromMemorySegment(newRectStorage)
            if (prev != rect) {
                self.moveWindow(
                    rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt(), 1
                )
                if (rect.area != prev.area) {
                    onResize(onRedraw)
                }
                prev = rect
            }

            if (!NativeAPI.isFirstDraw) {
                onResize(onRedraw)
            }
        }

        newRectStorage.close()
    }

    init {
        self.setWindowPosition(WindowManager.HWND_TOPMOST)
        checker.start()
    }

    fun remake(): Boolean {
        self.getWindowRect(currentRectStorage)
        rectReader = RectReader.fromMemorySegment(currentRectStorage)
        return if (rectReader.width > 0u && rectReader.height > 0u) {
            canvasWidth = rectReader.width.toInt()
            canvasHeight = rectReader.height.toInt()
            if (internal.width > 0 && internal.height > 0) {
                val old = internalGraphics
                val scaled = internal.getScaledInstance(canvasWidth, canvasHeight, BufferedImage.SCALE_SMOOTH)
                internal = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB)
                internalGraphics = internal.graphics
                internalGraphics.drawImage(scaled, 0, 0, null)
                old.dispose()
                true
            } else {
                internal = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB)
                internalGraphics = internal.graphics
                true
            }
        } else {
            false
        }
    }

    override fun image(image: BufferedImage, position: Overlay.Position, width: Int, height: Int) {
        internalGraphics.drawImage(image, position.x, position.y, width, height, null)
    }

    override fun clear() {
        internalGraphics.color = WindowManager.invisibleColor
        internalGraphics.fillRect(0, 0, canvasWidth, canvasHeight)
    }

    override fun close() {
        checker.interrupt()
        internalGraphics.dispose()
        currentRectStorage.close()
    }

    override fun show() {
        visible = true
        self.showWindow()
    }

    override fun hide() {
        visible = false
        self.hideWindow()
    }

    override fun publish() {

        ImageIO.write(internal, "bmp", storageBitmap.toFile())

        val bitmap = NativeAPI.loadImage(pathString)
        prevList = NativeAPI.imageListCreate(canvasWidth, canvasHeight, 0x00000001, 1, 1) as MemoryAddress

        NativeAPI.imageListAdd(prevList, bitmap, WindowManager.invisible)
        val old = NativeAPI.currentImageList

        NativeAPI.repaintLock.write {
            NativeAPI.currentImageList = prevList
            NativeAPI.images = 1
        }

        check(self.updateWindow()) {
            "Failed to update window: ${NativeAPI.getLastError()}"
        }

        NativeAPI.imageListDestroy(old)
    }

    private fun onResize(callback: (Overlay) -> Unit): Boolean {

        if (remake()) {
            callback(this)
            publish()
            return true
        }

        return false
    }
}