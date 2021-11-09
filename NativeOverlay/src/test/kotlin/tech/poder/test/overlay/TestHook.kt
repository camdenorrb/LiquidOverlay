package tech.poder.test.overlay

import tech.poder.overlay.Callback
import tech.poder.overlay.Overlay
import tech.poder.overlay.WindowManager
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
        WindowManager.createWindow(width = 100, height = 100, className = "button", windowName = "Hello")
    }


}