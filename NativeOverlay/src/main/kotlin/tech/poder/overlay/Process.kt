package tech.poder.overlay

import jdk.incubator.foreign.MemoryAddress

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
            NativeRegistry.registry[Callback.closeHandle].invoke(handle)
        }
    }
}
