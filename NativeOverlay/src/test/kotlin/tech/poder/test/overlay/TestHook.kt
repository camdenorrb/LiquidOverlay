package tech.poder.test.overlay

import tech.poder.overlay.*
import kotlin.test.Test

internal class TestHook {

    @Test
    fun basic() {
        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("notepad++.exe")) {
                i = index
            }
            println("$index: ${process.title}(${process.exeLocation})")
        }
        println("Choose: $i")
        val overlay = Overlay(processes[i])

        overlay.close()
    }

    @Test
    fun createWindow() {
        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("notepad++.exe")) {
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
        window.setWindowPosition(WindowManager.HWND_TOPMOST)
        var prev = selected.rect
        val checker = Thread {
            while (true) {
                Thread.sleep(500)
                selectedWindow.getWindowRect(rectScope)
                val rect = RectReader.fromMemorySegment(rectScope)
                if (prev != rect) {
                    println(
                        window.moveWindow(
                            rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt(), 1
                        )
                    )
                    prev = rect
                }
            }
        }
        checker.start()
        window.doLoop {
            selectedWindow.getWindowRect(rectScope)
            val rect = RectReader.fromMemorySegment(rectScope)
            window.moveWindow(rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt(), 1)
        }
        checker.interrupt()
        println("Window End")
        Thread.sleep(10000)
    }


}