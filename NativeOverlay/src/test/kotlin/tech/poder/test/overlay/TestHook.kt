package tech.poder.test.overlay

import jdk.incubator.foreign.MemoryAccess
import tech.poder.overlay.*
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.experimental.and
import kotlin.test.Test

internal class TestHook {

    /*
    @Test
    fun createWindow() {
        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("Overwatch.exe")) {
                i = index
            }
            println("$index: ${process.title}(${process.exeLocation})")
        }
        println("Choose: $i")
        val selected = processes[i]
        //Thread.sleep(10000)
        val clazz = WindowClass.define("Kats")
        println(selected.rect)
        val window = WindowManager.createWindow(
            WindowManager.WS_EX_TOPMOST or WindowManager.WS_EX_TRANSPARENT or WindowManager.WS_EX_LAYERED,
            clazz = clazz,
            windowName = "LiquidOverlay",
            style = WindowManager.WS_POPUP.toInt(),
            x = selected.rect.left.toInt(),
            y = selected.rect.top.toInt(),
            width = selected.rect.width.toInt(),
            height = selected.rect.height.toInt()
        )

        val rectScope = RectReader.createSegment()
        val selectedWindow = selected.asWindow()

        var prev = selected.rect
        val storageBitmap = Paths.get("test.bmp").toAbsolutePath()
        val pathString = ExternalStorage.fromString(storageBitmap.toString())
        val mainImage = BufferedImage(selected.rect.width.toInt(), selected.rect.height.toInt(), BufferedImage.TYPE_INT_RGB)


        repeat(mainImage.width) { x ->
            repeat(mainImage.height) { y ->
                mainImage.setRGB(x, y, WindowManager.invisibleRGB)
            }
        }
        /*
        repeat(mainImage.width / 2) { x ->
            repeat(mainImage.height / 2) { y ->
                mainImage.setRGB(x, y, Color.BLUE.rgb)
            }
        }*/

        val graphics = mainImage.createGraphics()
        graphics.color = Color(0, 100, 0)
        graphics.fillRect(0, 0, mainImage.width, mainImage.height)
        //.dispose()

        ImageIO.write(mainImage, "bmp", storageBitmap.toFile())
        val hbitmap = Callback.loadImage(pathString)
        Callback.lock.write {
            Callback.currentImageList = NativeRegistry[Callback.imageListCreate].invoke(prev.width.toInt(), prev.height.toInt(), 0x00000001, 1, 1) as MemoryAddress
            NativeRegistry[Callback.imageListAdd].invoke(Callback.currentImageList, hbitmap, WindowManager.invisible)
            Callback.images = 1
        }
        window.setWindowPosition(WindowManager.HWND_TOPMOST)

        val checker = Thread {
            while (selectedWindow.isAlive()) {
                Thread.sleep(10)
                if (selectedWindow.isVisible()) {
                    if (!window.isVisible()) {
                        window.setWindowPosition(WindowManager.HWND_TOPMOST)
                    }
                    selectedWindow.getWindowRect(rectScope)
                    val rect = RectReader.fromMemorySegment(rectScope)
                    if (prev != rect) {
                        window.moveWindow(
                            rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt(), 1
                        )
                        prev = rect
                        if (prev.area != rect.area) {
                            //REDO Images!
                        }
                    }
                } else {
                    window.setWindowPosition(WindowManager.HWND_BOTTOM)
                }
            }
        }

        checker.start()
        window.doLoop()
        checker.interrupt()
        println("Window End")
        Thread.sleep(10000)
    }*/

    @Test
    fun drawCat() {

        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("Notepad.exe")) {
                i = index
            }
            println("$index: ${process.title}(${process.exeLocation})")
        }
        println("Choose: $i")
        val selected = processes[i]
        val clazz = WindowClass.define("Kats")
        val window = WindowManager.createWindow(
            WindowManager.WS_EX_TOPMOST or WindowManager.WS_EX_TRANSPARENT or WindowManager.WS_EX_LAYERED,
            clazz = clazz,
            windowName = "LiquidOverlay",
            style = WindowManager.WS_POPUP.toInt(),
            x = selected.rect.left.toInt(),
            y = selected.rect.top.toInt(),
            width = selected.rect.width.toInt(),
            height = selected.rect.height.toInt()
        )
        val selectedWindow = selected.asWindow()

        val overlay = OverlayImpl(window, selectedWindow)

        overlay.onRedraw = {

            val bufferedImage = BufferedImage(it.canvasWidth, it.canvasHeight, BufferedImage.TYPE_INT_RGB)
            val graphics = bufferedImage.createGraphics()

            graphics.color = Color(0, 100, 0)
            graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

            it.image(bufferedImage, Overlay.Position(0, 0), it.canvasWidth, it.canvasHeight)

            graphics.dispose()
        }


        window.doLoop()

        overlay.close()
    }

    @Test
    fun nateDraw() {

        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("Overwatch.exe")) {
                i = index
            }
            println("$index: ${process.title}(${process.exeLocation})")
        }
        println("Choose: $i")
        val selected = processes[i]
        val clazz = WindowClass.define("Kats")
        val window = WindowManager.createWindow(
            WindowManager.WS_EX_TOPMOST or WindowManager.WS_EX_TRANSPARENT or WindowManager.WS_EX_LAYERED,
            clazz = clazz,
            windowName = "LiquidOverlay",
            style = WindowManager.WS_POPUP.toInt(),
            x = selected.rect.left.toInt(),
            y = selected.rect.top.toInt(),
            width = selected.rect.width.toInt(),
            height = selected.rect.height.toInt()
        )
        val selectedWindow = selected.asWindow()

        fun draw(overlay: Overlay) {

            val bufferedImage = BufferedImage(overlay.canvasWidth, overlay.canvasHeight, BufferedImage.TYPE_INT_RGB)
            val graphics = bufferedImage.createGraphics()

            graphics.color = Color(0, 100, 0)
            graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

            overlay.image(bufferedImage, Overlay.Position(0, 0), overlay.canvasWidth, overlay.canvasHeight)

            graphics.dispose()
        }

        val overlay = OverlayImpl(window, selectedWindow)

        overlay.onRedraw = {
            draw(it)
        }

        window.doLoop()
        overlay.close()
    }

    val AUDCLNT_BUFFERFLAGS_SILENT: Byte = 2

    fun getPNumFramesInPacket(state: StructInstance): UInt {
        return MemoryAccess.getIntAtOffset(state.segment, state[1]).toUInt()
    }

    fun getPFlags(state: StructInstance): Byte {
        return MemoryAccess.getByteAtOffset(state.segment, state[2])
    }

    fun getBitsPerSample(format: StructInstance): Short {
        return MemoryAccess.getShortAtOffset(format.segment, format[5])
    }

    fun getNumberOfChannels(format: StructInstance): Short {
        return MemoryAccess.getShortAtOffset(format.segment, format[1])
    }

    @Test
    fun basicAudio() {
        val state = Callback.newState()

        Callback.startRecording(state)
        val hnsPeriod = MemoryAccess.getDoubleAtOffset(state.segment, state[3])
        val sleepTime = ((hnsPeriod / 10_000.0) / 2.0).toLong()
        var counter = 0
        var dataLength = 0u
        val format = Callback.getFormat(state)

        val bytesPerFrame = ((getBitsPerSample(format) * getNumberOfChannels(format).toLong()) / 8L).toInt()

        while (counter < 30) {
            println("at top of loop")
            // Sleep for half the buffer duration.
            Thread.sleep(sleepTime)
            counter++
            Callback.getNextPacketSize(state)
            var packetLength = getPNumFramesInPacket(state)
            while (packetLength != 0u) {
                Callback.getBuffer(state)

                val flags = getPFlags(state)
                if (flags and AUDCLNT_BUFFERFLAGS_SILENT != 0.toByte()) {
                    println("SILENT")
                }
                dataLength += getPNumFramesInPacket(state)
                Callback.releaseBuffer(state)
                Callback.getNextPacketSize(state)
                packetLength = getPNumFramesInPacket(state)
            }
        }
        println(dataLength)
    }

}