package tech.poder.overlay.general

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.general.Callback
import tech.poder.overlay.general.NativeRegistry
import tech.poder.overlay.video.RectReader
import tech.poder.overlay.video.WindowManager

data class Process(
    val hWnd: MemoryAddress,
    val handle: MemoryAddress,
    val exeLocation: String,
    val clazz: String,
    val title: String,
    val pid: Int,
    val rect: RectReader,
): AutoCloseable {

    override fun close() {
        if (handle != MemoryAddress.NULL) {
            NativeRegistry[Callback.closeHandle].invoke(handle)
        }
    }

    fun asWindow(): WindowManager {
        return WindowManager(hWnd)
    }
    
}