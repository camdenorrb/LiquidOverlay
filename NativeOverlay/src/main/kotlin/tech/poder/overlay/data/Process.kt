package tech.poder.overlay.data

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.handles.WinAPIHandles
import tech.poder.overlay.window.WindowManager

data class Process(
    val hWnd: MemoryAddress,
    val handle: MemoryAddress,
    val exeLocation: String,
    val clazz: String,
    val title: String,
    val pid: Int,
    val rect: RectReader,
) : AutoCloseable {

    override fun close() {
        if (handle != MemoryAddress.NULL) {
            WinAPIHandles.closeHandle(handle)
        }
    }

    fun asWindow(): WindowManager {
        return WindowManager(hWnd)
    }

}