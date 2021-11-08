package tech.poder.test.overlay

import tech.poder.overlay.Callback
import tech.poder.overlay.Overlay
import kotlin.test.Test

class TestHook {
    @Test
    fun basic() {
        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("Notepad++")) {
                i = index
            }
            println("$index: ${process.title}(${process.exeLocation})")
        }
        println("Choose: $i")
        val overlay = Overlay(processes[i])
        repeat(9999) {
            overlay.startPaint()
            overlay.drawText("Hello World", 0, 0)
            overlay.endPaint()
            Thread.sleep(1)
        }
    }
}