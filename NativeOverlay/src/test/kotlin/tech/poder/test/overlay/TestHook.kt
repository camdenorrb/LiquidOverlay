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
        WindowManager.createWindow(clazz = clazz, windowName = "Hello", style = WindowManager.WS_OVERLAPPEDWINDOW, width = 100, height = 100)
        Thread.sleep(10000)
    }


}