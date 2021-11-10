package tech.poder.overlay

import jdk.incubator.foreign.MemoryAddress

@JvmInline
value class WindowClass(val clazzPointer: MemoryAddress) : AutoCloseable {
    companion object {
        fun fromStorage(externalStorage: ExternalStorage): WindowClass {
            return WindowClass(externalStorage.segment.address())
        }
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
