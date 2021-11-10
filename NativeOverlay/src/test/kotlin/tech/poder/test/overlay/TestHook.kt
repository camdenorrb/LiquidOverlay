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
        //Thread.sleep(10000)
        val clazz = WindowClass.define("Kats")
        WindowManager.createWindow(WindowManager.WS_EX_TOPMOST or WindowManager.WS_EX_TRANSPARENT or WindowManager.WS_EX_LAYERED, clazz = clazz, windowName = "LiquidOverlay", style = WindowManager.WS_POPUP.toInt(), width = 100, height = 100)
        Thread.sleep(10000)
    }


}